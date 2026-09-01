package ssu.eatssu.domain.auth.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class JwtAuthenticationFilterTest {

    private final JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtTokenProvider);

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void whitelistRequestWithoutTokenContinuesFilterChain() throws Exception {
        MockHttpServletRequest request = request("/menus/today", null);
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(chain.getRequest()).isSameAs(request);
    }

    @Test
    void whitelistRequestWithValidTokenSetsAuthentication() throws Exception {
        Authentication authentication = authentication();
        given(jwtTokenProvider.validateToken("token")).willReturn(true);
        given(jwtTokenProvider.getAuthentication("token")).willReturn(authentication);
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/oauths/valid/token", "Bearer token"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void whitelistRequestWithInvalidTokenStillContinuesFilterChainWithoutAuthentication() throws Exception {
        given(jwtTokenProvider.validateToken("expired")).willReturn(false);
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/oauths/valid/token", "Bearer expired"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void protectedRequestWithoutTokenReturnsUnauthorized() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/users/me", null), response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(response.getContentAsString()).contains("유효하지 않은 토큰입니다.");
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void protectedRequestWithInvalidTokenReturnsUnauthorized() throws Exception {
        given(jwtTokenProvider.validateToken("expired")).willReturn(false);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/users/me", "Bearer expired"), response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void protectedRequestWithValidTokenContinuesFilterChain() throws Exception {
        Authentication authentication = authentication();
        given(jwtTokenProvider.validateToken("token")).willReturn(true);
        given(jwtTokenProvider.getAuthentication("token")).willReturn(authentication);
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/users/me", "Bearer token"), new MockHttpServletResponse(), chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isSameAs(authentication);
        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void resolveTokenReturnsNullForNonBearerAuthorizationHeader() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/users/me", "Basic dXNlcjpwYXNz"), response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
    }

    @Test
    void resolveTokenReturnsNullWhenBearerHeaderHasNoTokenAfterIt() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request("/users/me", "Bearer"), response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
    }

    private MockHttpServletRequest request(String uri, String authorization) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        if (authorization != null) {
            request.addHeader("Authorization", authorization);
        }
        return request;
    }

    private Authentication authentication() {
        CustomUserDetails principal = new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER,
                DeviceType.IOS);
        return new UsernamePasswordAuthenticationToken(principal, "token", principal.getAuthorities());
    }
}
