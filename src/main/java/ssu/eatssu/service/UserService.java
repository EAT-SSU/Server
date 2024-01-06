package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.ReviewReport;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.OauthProvider;
import ssu.eatssu.domain.repository.ReviewReportRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.handler.response.BaseResponseStatus;
import ssu.eatssu.jwt.JwtTokenProvider;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.web.user.dto.Tokens;

import static ssu.eatssu.handler.response.BaseResponseStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 닉네임 변경
     */
    public void updateNickname(Long userId, String nickname) {

        User user = userRepository.findById(userId)
                        .orElseThrow(()-> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        user.updateNickname(nickname);
        userRepository.save(user);
    }

    /**
     * Oauth 회원 - email, providerId 를 통해 JwtToken 을 생성
     * todo: 같은 이메일로 카카오, 애플 등 여러 회원가입을 한 회원 처리 필요
     */
    public Tokens generateOauthJwtTokens(String email, OauthProvider provider, String providerId) {

        // email, credentials 를 기반으로 Authentication 객체 생성
        // 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(email, makeOauthCredentials(provider, providerId));

        // 실제 검증 (사용자 비밀번호 체크)
        // authenticate 메서드 실행 => CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 바탕으로 JWT 토큰 생성
        return jwtTokenProvider.generateTokens(authentication);
    }

    private String makeOauthCredentials(OauthProvider provider, String providerId) {
        return provider+"_"+providerId;
    }

    /**
     * JWT 토큰 재발급
     */
    public Tokens refreshTokens(Authentication authentication) {
        return jwtTokenProvider.generateTokens(authentication);
    }

    /**
     * 유저 탈퇴
     */
    public void signout (Long userId) {
        User user = userRepository.findById(userId).orElseThrow(()-> new BaseException(NOT_FOUND_USER));

        //작성한 리뷰 삭제
        for(Review review: user.getReviews()) {
            review.signoutUser();
        }
        //작성한 신고 삭제
        for(ReviewReport report: user.getReviewReports()){
            reviewReportRepository.delete(report);
        }
        /*
        todo: 탈퇴한 유저의 문의내역은 어떻게 처리??
        for(UserInquiries inquiries : user.getUserInquiries()){
            inquiries.signoutWriter();
        }*/
        userRepository.delete(user);
    }
}
