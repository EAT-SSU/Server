package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.dto.request.LoginRequest;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.response.Tokens;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {

    @Test
    void loginDelegatesCredentialsToTokenProvider() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        Tokens tokens = new Tokens("access", "refresh");
        given(tokenProvider.generateTokens("admin", "password")).willReturn(tokens);
        AuthenticationService service = new AuthenticationService(tokenProvider);

        assertThat(service.login(new LoginRequest("admin", "password"))).isSameAs(tokens);
    }
}
