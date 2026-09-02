package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.request.LoginRequest;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.response.Tokens;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
    private final JwtTokenProvider tokenProvider;

    public Tokens login(LoginRequest request) {
        return tokenProvider.generateTokens(request.loginId(), request.password());
    }
}
