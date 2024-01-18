package ssu.eatssu.domain.auth.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.entity.AppleAuthenticator;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.domain.auth.dto.OAuthInfo;
import ssu.eatssu.domain.user.dto.Tokens;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AppleAuthenticator appleAuthenticator;

    public Tokens kakaoLogin(KakaoLoginRequest request) {
        User user = userRepository.findByProviderId(request.providerId())
            .orElseGet(() -> join(request.email(), OAuthProvider.KAKAO, request.providerId()));

        return userService.generateOauthJwtTokens(user.getEmail(), OAuthProvider.KAKAO,
            request.providerId());
    }

    public Tokens appleLogin(AppleLoginRequest request) {
        OAuthInfo oAuthInfo = appleAuthenticator.getOAuthInfoByIdentityToken(
            request.identityToken());

        User user = userRepository.findByProviderId(oAuthInfo.providerId())
            .orElseGet(() -> join(oAuthInfo.email(), OAuthProvider.APPLE, oAuthInfo.providerId()));

        //todo 이메일 갱신의 이유?
        updateAppleUserEmail(user, oAuthInfo.email());

        return userService.generateOauthJwtTokens(user.getEmail(), OAuthProvider.APPLE,
            oAuthInfo.providerId());
    }

    private User join(String email, OAuthProvider provider, String providerId) {
        String credentials = createCredentials(provider, providerId);

        User user = User.oAuthJoin(email, provider, providerId, credentials);
        return userRepository.save(user);
    }

    private String createCredentials(OAuthProvider provider, String providerId) {
        return passwordEncoder.encode(provider + "_" + providerId);
    }

    private void updateAppleUserEmail(User user, String email) {
        if (isHideEmail(user.getEmail()) && !isHideEmail(email)) {
            user.updateEmail(email);
            userRepository.save(user);
        }
    }

    private boolean isHideEmail(String email) {
        if (email.length() > 25) {
            return email.startsWith("@privaterelay.appleid.com", email.length() - 25);
        } else {
            return false;
        }
    }
}
