package ssu.eatssu.domain.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ssu.eatssu.domain.auth.security.JwtAuthenticationFilter;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.global.handler.JwtAccessDeniedHandler;
import ssu.eatssu.global.handler.JwtAuthenticationEntryPoint;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String[] RESOURCE_LIST = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/oauths/valid/token", "/admin/img/**", "/css/**", "/js/**",
            "/favicon.ico", "/error/**", "/webjars/**", "/h2-console/**"
    };

    private static final String[] SWAGGER_PATHS = {
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**"
    };

    private static final String[] AUTH_WHITELIST = {
            "/", "/oauths/kakao", "/oauths/apple", "/menus/**", "/meals/**", "/admin/login", "/oauths/v2/kakao","/oauths/v2/apple",
            "/reviews", "/reviews/menus/**", "/reviews/meals/**", "/v2/reviews/statistics/**",
            "/v2/reviews/menus/**", "/v2/reviews/meals/**", "/actuator/health", "/actuator/prometheus",
            "/good-price-stores", "/good-price-stores/**"
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
    @Order(1)
    @Profile({"dev", "prod"})
    public SecurityFilterChain swaggerFilterChain(HttpSecurity http,
                                                    UserDetailsService swaggerUserDetailsService) throws Exception {
        http
                .securityMatcher(SWAGGER_PATHS)
                .csrf(csrf -> csrf.disable())
                .userDetailsService(swaggerUserDetailsService)
                .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    @Profile({"dev", "prod"})
    public UserDetailsService swaggerUserDetailsService(@Value("${swagger.username}") String username,
                                                          @Value("${swagger.password}") String password,
                                                          PasswordEncoder passwordEncoder) {
        UserDetails swaggerUser = User.withUsername(username)
                .password(passwordEncoder.encode(password))
                .roles("SWAGGER")
                .build();
        return new InMemoryUserDetailsManager(swaggerUser);
    }

    @Bean
    @Order(2)
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .shouldFilterAllDispatcherTypes(false)
                        .requestMatchers("/partnerships/*/like").authenticated()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .requestMatchers(RESOURCE_LIST).permitAll()
                        .requestMatchers(ADMIN_PAGE_LIST).hasRole("ADMIN")
                        .anyRequest().authenticated()
                        .and().addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                                               UsernamePasswordAuthenticationFilter.class))
                .exceptionHandling()
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .authenticationEntryPoint(jwtAuthenticationEntryPoint);

        http
                .cors().configurationSource(corsConfigurationSource());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
