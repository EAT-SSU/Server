package ssu.eatssu.global.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ssu.eatssu.domain.admin.dto.request.LoginRequest;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.dto.response.Tokens;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponse;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ControllerLogAspectTest {

    private final ControllerLogAspect controllerLogAspect = new ControllerLogAspect(new ObjectMapper(), null);

    @AfterEach
    void clearContext() {
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldSkipResponseBodyForOauthApi() throws Exception {
        // given
        BaseResponse<Tokens> response = BaseResponse.success(new Tokens("access-token", "refresh-token"));

        // when
        String result = controllerLogAspect.getResponseLog("/oauths/kakao", response);

        // then
        assertThat(result).isEqualTo("[response-body-skipped]");
        assertThat(result).doesNotContain("access-token", "refresh-token");
    }

    @Test
    void shouldLogResponseBodyForNonOauthApi() throws Exception {
        // given
        BaseResponse<String> response = BaseResponse.success("ok");

        // when
        String result = controllerLogAspect.getResponseLog("/meals", response);

        // then
        assertThat(result).contains("ok");
    }

    @Test
    void shouldMaskAnnotatedRequestFields() throws Exception {
        // given
        LoginRequest request = new LoginRequest("admin", "password");

        // when
        Map<String, Object> result = controllerLogAspect.toSafeMap(request);

        // then
        assertThat(result).containsEntry("loginId", "admin");
        assertThat(result).containsEntry("password", "***");
    }

    @Test
    void 긴_응답은_잘라서_로그에_남긴다() {
        String result = controllerLogAspect.getResponseLog("/menus", "x".repeat(700));

        assertThat(result).endsWith("...(truncated)").hasSizeGreaterThan(600);
    }

    @Test
    void 직렬화할_수_없는_응답은_toString으로_로그를_남긴다() {
        Object response = new Object();

        assertThat(controllerLogAspect.getResponseLog("/menus", response)).isEqualTo(response.toString());
    }

    @Test
    void 컨트롤러_정상_호출을_진행한다() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/menus");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CustomUserDetails user = new CustomUserDetails(1L, "user@test.com", "pw", Role.USER, DeviceType.IOS);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"request", "requestBody"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{request, new LoginRequest("admin", "password")});
        when(joinPoint.proceed()).thenReturn("ok");

        assertThat(controllerLogAspect.logApi(joinPoint)).isEqualTo("ok");
    }

    @Test
    void 컨트롤러_예외를_슬랙에_알리고_다시_던진다() throws Throwable {
        SlackErrorNotifier notifier = mock(SlackErrorNotifier.class);
        ControllerLogAspect aspect = new ControllerLogAspect(new ObjectMapper(), notifier);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/menus");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[0]);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenThrow(new BaseException(BaseResponseStatus.NOT_FOUND_MENU));

        assertThatThrownBy(() -> aspect.logApi(joinPoint)).isInstanceOf(BaseException.class);
        verify(notifier).notify(any(), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void 제외_인자와_null_인자와_긴_인자를_안전하게_로그한다() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/menus");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        CustomUserDetails user = new CustomUserDetails(1L, "user@test.com", "pw", Role.USER, null);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(null);
        when(joinPoint.getArgs()).thenReturn(new Object[]{request, user, mock(BindingResult.class), null, new LongRequest("x".repeat(300))});
        when(joinPoint.proceed()).thenReturn("ok");

        assertThat(controllerLogAspect.logApi(joinPoint)).isEqualTo("ok");
    }

    @Test
    void 요청_인자_직렬화에_실패하면_toString으로_대체한다() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/menus");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[]{"body"});
        when(joinPoint.getArgs()).thenReturn(new Object[]{new RecursiveRequest(), null});
        when(joinPoint.proceed()).thenReturn("ok");

        assertThat(controllerLogAspect.logApi(joinPoint)).isEqualTo("ok");
    }

    @Test
    void 인증되지_않은_사용자와_메시지_없는_예외을_처리한다() throws Throwable {
        SlackErrorNotifier notifier = mock(SlackErrorNotifier.class);
        ControllerLogAspect aspect = new ControllerLogAspect(new ObjectMapper(), notifier);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Authentication authentication = mock(Authentication.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/menus");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[0]);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> aspect.logApi(joinPoint)).isInstanceOf(RuntimeException.class);
        verify(notifier).notify(any(), anyString(), anyString(), org.mockito.ArgumentMatchers.eq("anonymous"), anyString());
    }

    @Test
    void CustomUserDetails가_아닌_principal은_익명으로_처리한다() throws Throwable {
        SlackErrorNotifier notifier = mock(SlackErrorNotifier.class);
        ControllerLogAspect aspect = new ControllerLogAspect(new ObjectMapper(), notifier);
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);
        Authentication authentication = mock(Authentication.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/menus");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn("user");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getParameterNames()).thenReturn(new String[0]);
        when(joinPoint.getArgs()).thenReturn(new Object[0]);
        when(joinPoint.proceed()).thenThrow(new IllegalStateException("failed"));

        assertThatThrownBy(() -> aspect.logApi(joinPoint)).isInstanceOf(IllegalStateException.class);
        verify(notifier).notify(any(), anyString(), anyString(), org.mockito.ArgumentMatchers.eq("anonymous"), anyString());
    }

    @Test
    void null_URI도_일반_응답으로_로그한다() {
        assertThat(controllerLogAspect.getResponseLog(null, "ok")).contains("ok");
    }

    @Test
    void 포인트컷_선언은_예외없이_호출된다() {
        controllerLogAspect.restController();
    }

    private record LongRequest(String value) { }

    private static class RecursiveRequest {
        private final Object self = this;
    }

}
