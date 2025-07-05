package ssu.eatssu.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final List<String> AUTH_WHITELIST = List.of(
            "/",
            "/oauths/kakao",
            "/oauths/apple",
            "/menus/**",
            "/meals/**",
            "/admin/login",
            "/reviews",
            "/reviews/menus/**",
            "/reviews/meals/**",
            "/v2/reviews/statistics/**",
            "/v2/reviews/menus/**",
            "/v2/reviews/meals/**",
            "/actuator/**",
            "/error-test/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/oauths/valid/token",
            "/admin/img/**",
            "/css/**",
            "/js/**",
            "/favicon.ico",
            "/error/**",
            "/webjars/**",
            "/h2-console/**"
                                                              );
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        if (isWhiteListed(requestURI)) {
            log.info("화이트리스트 통과: {}", requestURI);
            chain.doFilter(request, response);
            return;
        }

        String token = resolveToken(httpRequest);

        if (token == null) {
            log.warn("토큰 없음: {}", requestURI);
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        if (!jwtTokenProvider.validateToken(token)) {
            log.warn("토큰 유효하지 않음: {}", requestURI);
            throw new BaseException(BaseResponseStatus.INVALID_TOKEN);
        }

        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        chain.doFilter(request, response);
    }


    private boolean isWhiteListed(String requestURI) {
        return AUTH_WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    // Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
