package ssu.eatssu.domain.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * 인증 정보를 바탕으로 Authentication 객체 생성
 */
@RequiredArgsConstructor
@Component
public class UserAuthenticationProvider {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public Authentication getAuthentication(String providerId, String rawPassword) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(providerId, rawPassword);

        return authenticationManagerBuilder
                .eraseCredentials(true)
                .getObject()
                .authenticate(authenticationToken);
    }
}
