package ssu.eatssu.domain.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ssu.eatssu.domain.auth.security.JwtAuthenticationFilter;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.global.handler.JwtAccessDeniedHandler;
import ssu.eatssu.global.handler.JwtAuthenticationEntryPoint;


@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] RESOURCE_LIST = {
        "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**","/admin/img/**","/css/**", "/js/**",
            "/favicon.ico", "/error/**", "/webjars/**", "/h2-console/**"
    };

    private static final String[] AUTH_WHITELIST = {
            "/","/oauths/**", "/user/join", "/user/login", "/user/user-emails/{email}/exist", "/user/check-nickname",
            "/menu/**", "/restaurants/**", "/review/info","/review/list", "/oauth/**", "/inquiries/{userInquiriesId}",
            "/inquiries/list", "/admin/login"
    };

    private static final String[] ADMIN_PAGE_LIST = {
            "/admin/**"
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .shouldFilterAllDispatcherTypes(false)
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(RESOURCE_LIST).permitAll()
                        .requestMatchers(ADMIN_PAGE_LIST).hasRole("ADMIN")
                        .anyRequest().authenticated()
                        .and().addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                UsernamePasswordAuthenticationFilter.class))
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);
        return http.build();
    }

}
