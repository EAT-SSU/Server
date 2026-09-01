package ssu.eatssu.global.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;

import static org.assertj.core.api.Assertions.assertThat;

class JwtFailureHandlerTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void 인증_실패는_401_json을_반환한다() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtAuthenticationEntryPoint(objectMapper).commence(new MockHttpServletRequest(), response,
                                                               new BadCredentialsException("invalid"));

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentType()).startsWith("application/json");
        assertThat(response.getContentAsString()).contains("\"code\":401");
    }

    @Test
    void 인가_실패는_403_json을_반환한다() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        new JwtAccessDeniedHandler(objectMapper).handle(new MockHttpServletRequest(), response,
                                                        new AccessDeniedException("forbidden"));

        assertThat(response.getStatus()).isEqualTo(403);
        assertThat(response.getContentAsString()).contains("\"code\":403");
    }
}
