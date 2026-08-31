package ssu.eatssu.domain.auth.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ssu.eatssu.domain.user.dto.response.Tokens;
import ssu.eatssu.domain.user.entity.DeviceType;

import java.util.Base64;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtTokenProviderTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("test-jwt-secret-key-with-enough-length-for-hs512-signing-1234567890".getBytes());

    private UserAuthenticationProvider authenticationProvider;

    @BeforeEach
    void setUp() {
        authenticationProvider = mock(UserAuthenticationProvider.class);
    }

    private JwtTokenProvider createProvider(long accessTokenValiditySeconds, long refreshTokenValiditySeconds) {
        JwtTokenProvider provider = new JwtTokenProvider(SECRET, accessTokenValiditySeconds,
                                                          refreshTokenValiditySeconds, authenticationProvider);
        provider.setKey();
        return provider;
    }

    private Authentication createAuthentication() {
        CustomUserDetails principal = new CustomUserDetails(1L, "test@test.com", "", new SimpleGrantedAuthority("ROLE_USER"),
                                                             DeviceType.IOS);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    @Test
    void generateTokensFromAuthenticationCreatesValidTokens() throws Exception {
        // given
        JwtTokenProvider provider = createProvider(3600, 604800);

        // when
        Tokens tokens = provider.generateTokens(createAuthentication());

        // then
        assertThat(provider.validateToken(tokens.accessToken())).isTrue();
        assertThat(provider.validateToken(tokens.refreshToken())).isTrue();

        Authentication parsed = provider.getAuthentication(tokens.accessToken());
        CustomUserDetails principal = (CustomUserDetails) parsed.getPrincipal();
        assertThat(principal.getId()).isEqualTo(1L);
        assertThat(principal.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    void generateTokensFromProviderIdDelegatesToAuthenticationProvider() {
        // given
        JwtTokenProvider provider = createProvider(3600, 604800);
        when(authenticationProvider.getAuthentication("provider-id", "password"))
                .thenReturn(createAuthentication());

        // when
        Tokens tokens = provider.generateTokens("provider-id", "password");

        // then
        assertThat(provider.validateToken(tokens.accessToken())).isTrue();
    }

    @Test
    void validateTokenReturnsFalseForExpiredToken() throws InterruptedException {
        // given
        JwtTokenProvider provider = createProvider(0, 604800);
        Tokens tokens = provider.generateTokens(createAuthentication());
        Thread.sleep(10);

        // when
        boolean isValid = provider.validateToken(tokens.accessToken());

        // then
        assertThat(isValid).isFalse();
    }

    @Test
    void validateTokenReturnsFalseForMalformedToken() {
        // given
        JwtTokenProvider provider = createProvider(3600, 604800);

        // when
        boolean isValid = provider.validateToken("not-a-valid-jwt");

        // then
        assertThat(isValid).isFalse();
    }
}
