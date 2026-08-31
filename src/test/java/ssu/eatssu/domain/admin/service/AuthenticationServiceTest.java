package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import ssu.eatssu.domain.admin.controller.AdminAuth;
import ssu.eatssu.domain.admin.dto.request.LoginRequest;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.response.Tokens;
import ssu.eatssu.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {

    @Test
    void loginDelegatesCredentialsToTokenProvider() {
        JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
        Tokens tokens = new Tokens("access", "refresh");
        given(tokenProvider.generateTokens("admin", "password")).willReturn(tokens);
        AuthenticationService service = new AuthenticationService(tokenProvider, mock(PasswordEncoder.class),
                mock(UserRepository.class), new AdminAuth("admin"));

        assertThat(service.login(new LoginRequest("admin", "password"))).isSameAs(tokens);
    }
}
