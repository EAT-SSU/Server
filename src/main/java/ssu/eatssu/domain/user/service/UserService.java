package ssu.eatssu.domain.user.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.auth.entity.CustomUserDetails;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.dto.MyPageResponse;
import ssu.eatssu.domain.user.dto.UpdateNicknameRequest;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.auth.entity.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.global.handler.response.BaseException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public void updateNickname(CustomUserDetails userDetails, UpdateNicknameRequest request) {
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        user.updateNickname(request.nickname());
        userRepository.save(user);
    }

    public MyPageResponse findMyPage(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        return new MyPageResponse(user.getNickname(), user.getProvider());
    }

    public boolean withdraw(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        user.getReviews().forEach(Review::clearUser);
        userRepository.delete(user);

        return true;
    }

    public Boolean validateDuplicatedEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    public Boolean validateDuplicatedNickname(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    /**
     * Oauth 회원 - email, providerId 를 통해 JwtToken 을 생성
     * todo: 같은 이메일로 카카오, 애플 등 여러 회원가입을 한 회원 처리 필요
     */
    public Tokens generateOauthJwtTokens(String email, OAuthProvider provider, String providerId) {

        // email, credentials 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
            new UsernamePasswordAuthenticationToken(email,
	makeOauthCredentials(provider, providerId));

        // 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드 실행 => CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject()
            .authenticate(authenticationToken);

        // 인증 정보를 바탕으로 JWT 토큰 생성
        return jwtTokenProvider.generateTokens(authentication);
    }

    private String makeOauthCredentials(OAuthProvider provider, String providerId) {
        return provider + "_" + providerId;
    }

    /**
     * JWT 토큰 재발급
     */
    public Tokens refreshTokens(Authentication authentication) {
        return jwtTokenProvider.generateTokens(authentication);
    }
}
