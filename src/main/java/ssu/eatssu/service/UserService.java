package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.ReviewReport;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.ReviewReportRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.handler.response.BaseResponseStatus;
import ssu.eatssu.jwt.JwtTokenProvider;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.web.user.dto.Tokens;

import static ssu.eatssu.handler.response.BaseResponseStatus.*;

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
     * 자체 회원가입
     *
     * @deprecated
     */
    public Tokens join(String email, String pwd) {

        String encodedPwd = passwordEncoder.encode(pwd);

        User user = User.join(email, encodedPwd);
        userRepository.save(user);

        return generateJwtTokens(email, pwd);
    }

    /**
     * 자체 로그인
     *
     * @deprecated
     */
    public Tokens login(String email, String pwd) {
        //가입된 유저인지 체크
        userRepository.findByEmail(email).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        return generateJwtTokens(email, pwd);
    }

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
     * email, pwd 를 통해 JwtToken 을 생성
     */
    public Tokens generateJwtTokens(String email, String pwd) {
        // 1. email/pwd 를 기반으로 Authentication 객체 생성
        //    이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, pwd);

        // 2. 실제 검증 (사용자 비밀번호 체크)
        //    authenticate 메서드 실행 => CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 바탕으로 JWT 토큰 생성
        return jwtTokenProvider.generateTokens(authentication);
    }

    /**
     * 비밀번호 변경
     */
    public void updatePassword(Long userId, String pwd) {
        User user = userRepository.findById(userId)
              .orElseThrow(()-> new BaseException(BaseResponseStatus.NOT_FOUND_USER));

        String encodedPwd = passwordEncoder.encode(pwd);
        user.changePassword(encodedPwd);

        userRepository.save(user);
    }

    /**
     * JWT 토큰 재발급
     */
    public Tokens refreshTokens(Authentication authentication) {
        return jwtTokenProvider.generateTokens(authentication);
    }

    /**
     * 유저 탈퇴 //todo
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
        TODO 작성한 문의내역 삭제??
        for(UserInquiries inquiries : user.getUserInquiries()){
            inquiries.signoutWriter();
        }*/
        userRepository.delete(user);
    }
}
