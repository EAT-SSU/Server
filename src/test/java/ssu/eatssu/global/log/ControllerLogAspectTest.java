package ssu.eatssu.global.log;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.dto.LoginRequest;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ControllerLogAspectTest {

    private final ControllerLogAspect controllerLogAspect = new ControllerLogAspect(new ObjectMapper(), null);

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

}
