package ssu.eatssu.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.OAuthInfo;
import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.auth.entity.AppleAuthenticator;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.global.handler.response.BaseException;

import static ssu.eatssu.domain.auth.entity.OAuthProvider.APPLE;
import static ssu.eatssu.domain.auth.entity.OAuthProvider.KAKAO;

@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";
    private final UserService userService;
    private final UserRepository userRepository;
    private final AppleAuthenticator appleAuthenticator;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public Tokens kakaoLogin(KakaoLoginRequest request) {
        User user = userRepository.findByProviderId(request.providerId())
                                  .orElseGet(() -> userService.join(request.email(), KAKAO, request.providerId()));

        return generateOauthJwtTokens(user.getEmail(), KAKAO, request.providerId());
    }

    public Tokens appleLogin(AppleLoginRequest request) {
        OAuthInfo oAuthInfo = appleAuthenticator.getOAuthInfoByIdentityToken(request.identityToken());

        User user = userRepository.findByProviderId(oAuthInfo.providerId())
                                  .orElseGet(() -> userService.join(oAuthInfo.email(), APPLE, oAuthInfo.providerId()));

        updateAppleUserEmail(user, oAuthInfo.email());

        return generateOauthJwtTokens(user.getEmail(), APPLE, oAuthInfo.providerId());
    }

    public Tokens refreshTokens(Authentication authentication) {
        Tokens tokens = jwtTokenProvider.generateTokens(authentication);
        return tokens;
    }

    private void updateAppleUserEmail(User user, String email) {
        if (isHideEmail(user.getEmail()) && !isHideEmail(email)) {
            user.updateEmail(email);
            userRepository.save(user);
        }
    }

    public Boolean validToken(ValidRequest request) {
        String token = request.token();

        try {
            return jwtTokenProvider.validateToken(token);
        } catch (BaseException e) {
            return false;
        }
    }

    private boolean isHideEmail(String email) {
        if (email.length() > 25) {
            return email.startsWith("@privaterelay.appleid.com", email.length() - 25);
        } else {
            return false;
        }
    }

    // FIXME: 같은 이메일로 카카오, 애플 등 여러 회원가입을 한 회원 처리 필요.
    // 사용자는 같은 이메일로 로그인을 진행했을 시 같은 계정에 접근하기를 원함.
    // iPhone을 사용하는 사용자들에서만 한함.
    // 하지만 private relay를 사용하는 경우도 있는데, 이럴 때는 사용자의 계정을 묶기 다소 복잡하다.
    private Tokens generateOauthJwtTokens(String email, OAuthProvider provider, String providerId) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, makeOauthCredentials(provider, providerId));

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        Tokens tokens = jwtTokenProvider.generateTokens(authentication);
        return tokens;
    }

    private String makeOauthCredentials(OAuthProvider provider, String providerId) {
        return provider + providerId;
    }

}
