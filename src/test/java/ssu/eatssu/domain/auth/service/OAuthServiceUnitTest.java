package ssu.eatssu.domain.auth.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.auth.entity.AppleAuthenticator;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.global.handler.response.BaseException;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class OAuthServiceUnitTest {

    @InjectMocks
    private OAuthService oAuthService;

    @Mock
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppleAuthenticator appleAuthenticator;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("Apple 프라이빗 릴레이 이메일 감지 - 정확한 패턴")
    void isHideEmail_프라이빗릴레이이메일_true반환() throws Exception {
        // given
        String privateEmail = "abc123@privaterelay.appleid.com";

        // when
        boolean result = invokeIsHideEmail(privateEmail);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("일반 이메일 감지 - 프라이빗 릴레이 아님")
    void isHideEmail_일반이메일_false반환() throws Exception {
        // given
        String normalEmail = "user@gmail.com";

        // when
        boolean result = invokeIsHideEmail(normalEmail);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("짧은 이메일 처리 - 25자 이하")
    void isHideEmail_짧은이메일_false반환() throws Exception {
        // given
        String shortEmail = "a@privaterelay.appleid.com"; // 25자 정확히

        // when
        boolean result = invokeIsHideEmail(shortEmail);

        // then
        assertThat(result).isFalse();
    }

    @ParameterizedTest
    @DisplayName("다양한 프라이빗 릴레이 이메일 패턴 테스트")
    @ValueSource(strings = {
            "test123@privaterelay.appleid.com",
            "a1b2c3d4e5f6@privaterelay.appleid.com",
            "verylongusername@privaterelay.appleid.com"
    })
    void isHideEmail_다양한프라이빗릴레이_true반환(String email) throws Exception {
        // when
        boolean result = invokeIsHideEmail(email);

        // then
        assertThat(result).isTrue();
    }

    @ParameterizedTest
    @DisplayName("유사하지만 다른 도메인 이메일 테스트")
    @ValueSource(strings = {
            "user@privaterelay.apple.com",  // 잘못된 도메인
            "user@privaterelay.appleid.co", // 잘못된 TLD
            "user@privaterelay.appleid.comm", // 오타
            "user@private.appleid.com"      // 다른 서브도메인
    })
    void isHideEmail_유사한도메인_false반환(String email) throws Exception {
        // when
        boolean result = invokeIsHideEmail(email);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("OAuth credential 생성 테스트")
    void makeOauthCredentials_올바른형식_반환() throws Exception {
        // given
        OAuthProvider provider = OAuthProvider.KAKAO;
        String providerId = "12345";

        // when
        String result = invokeMakeOauthCredentials(provider, providerId);

        // then
        assertThat(result).isEqualTo("KAKAO12345");
    }

    @Test
    @DisplayName("Apple OAuth credential 생성 테스트")
    void makeOauthCredentials_애플_올바른형식_반환() throws Exception {
        // given
        OAuthProvider provider = OAuthProvider.APPLE;
        String providerId = "apple_user_123";

        // when
        String result = invokeMakeOauthCredentials(provider, providerId);

        // then
        assertThat(result).isEqualTo("APPLEapple_user_123");
    }

    @Test
    @DisplayName("유효한 토큰 검증 - 성공 케이스")
    void validToken_유효한토큰_true반환() {
        // given
        String validToken = "valid.jwt.token";
        ValidRequest request = new ValidRequest(validToken);
        given(jwtTokenProvider.validateToken(validToken)).willReturn(true);

        // when
        Boolean result = oAuthService.validToken(request);

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("유효하지 않은 토큰 검증 - 실패 케이스")
    void validToken_무효한토큰_false반환() {
        // given
        String invalidToken = "invalid.jwt.token";
        ValidRequest request = new ValidRequest(invalidToken);
        given(jwtTokenProvider.validateToken(invalidToken)).willReturn(false);

        // when
        Boolean result = oAuthService.validToken(request);

        // then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("토큰 검증 중 예외 발생 - false 반환")
    void validToken_예외발생_false반환() {
        // given
        String problemToken = "problem.jwt.token";
        ValidRequest request = new ValidRequest(problemToken);
        given(jwtTokenProvider.validateToken(problemToken))
                .willThrow(new BaseException(null));

        // when
        Boolean result = oAuthService.validToken(request);

        // then
        assertThat(result).isFalse();
    }

    // 리플렉션을 통한 private 메소드 테스트 헬퍼
    private boolean invokeIsHideEmail(String email) throws Exception {
        Method method = OAuthService.class.getDeclaredMethod("isHideEmail", String.class);
        method.setAccessible(true);
        return (boolean) method.invoke(oAuthService, email);
    }

    private String invokeMakeOauthCredentials(OAuthProvider provider, String providerId) throws Exception {
        Method method = OAuthService.class.getDeclaredMethod("makeOauthCredentials", OAuthProvider.class, String.class);
        method.setAccessible(true);
        return (String) method.invoke(oAuthService, provider, providerId);
    }
} 