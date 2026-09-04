package ssu.eatssu.domain.auth.security;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserAuthenticationProviderTest {

    private static final PasswordEncoder PASSWORD_ENCODER = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    @Test
    void getAuthenticationReturnsAuthenticatedTokenForValidCredentials() throws Exception {
        UserAuthenticationProvider provider = new UserAuthenticationProvider(authenticationManager("provider-id", "password"));

        Authentication result = provider.getAuthentication("provider-id", "password");

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getName()).isEqualTo("provider-id");
    }

    @Test
    void getAuthenticationThrowsForInvalidCredentials() throws Exception {
        UserAuthenticationProvider provider = new UserAuthenticationProvider(authenticationManager("provider-id", "password"));

        assertThatThrownBy(() -> provider.getAuthentication("provider-id", "wrong-password"))
                .isInstanceOf(BadCredentialsException.class);
    }

    private AuthenticationManager authenticationManager(String providerId, String rawPassword) throws Exception {
        UserDetailsService userDetailsService = username ->
                new User(providerId, PASSWORD_ENCODER.encode(rawPassword), List.of());
        DaoAuthenticationProvider daoProvider = new DaoAuthenticationProvider();
        daoProvider.setUserDetailsService(userDetailsService);
        daoProvider.setPasswordEncoder(PASSWORD_ENCODER);

        ObjectPostProcessor<Object> noOpPostProcessor = new ObjectPostProcessor<>() {
            @Override
            public <T> T postProcess(T object) {
                return object;
            }
        };
        AuthenticationManagerBuilder builder = new AuthenticationManagerBuilder(noOpPostProcessor);
        builder.authenticationProvider(daoProvider);
        return builder.build();
    }
}
