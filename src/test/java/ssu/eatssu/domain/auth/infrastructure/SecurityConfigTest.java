package ssu.eatssu.domain.auth.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.global.handler.JwtAccessDeniedHandler;
import ssu.eatssu.global.handler.JwtAuthenticationEntryPoint;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(
            mock(JwtTokenProvider.class), mock(JwtAccessDeniedHandler.class), mock(JwtAuthenticationEntryPoint.class));

    @Test
    void swaggerUserDetailsServiceEncodesPasswordAndExposesUsername() {
        PasswordEncoder passwordEncoder = securityConfig.passwordEncoder();

        UserDetailsService userDetailsService =
                securityConfig.swaggerUserDetailsService("eatssu-dev", "eatssu2026!", passwordEncoder);
        UserDetails user = userDetailsService.loadUserByUsername("eatssu-dev");

        assertThat(user.getUsername()).isEqualTo("eatssu-dev");
        assertThat(passwordEncoder.matches("eatssu2026!", user.getPassword())).isTrue();
    }
}
