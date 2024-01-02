package ssu.eatssu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ssu.eatssu.jwt.JwtTokenProvider;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.web.user.dto.Tokens;

import java.util.Optional;

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
    public Tokens join(String email, String pwd) throws JsonProcessingException{
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
    public Tokens login(String email, String pwd) throws JsonProcessingException {
        //유저 존재 여부 체크
        Optional<User> user = userRepository.findByEmail(email);
        if(user.isEmpty()){
            throw new BaseException(NOT_FOUND_USER);
        }
        return generateJwtTokens(email, pwd);
    }

    /**
     * 닉네임 변경
     */
    public void updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
              .orElseThrow(()-> new RuntimeException("User not found"));
        user.updateNickname(nickname);
        userRepository.save(user);
    }

    /**
     * email, pwd를 통해 JwtToken을 생성 //todo: JwtTokenProvider로 옮길까?
     */
    public Tokens generateJwtTokens(String email, String pwd) throws JsonProcessingException {
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
              .orElseThrow(()-> new RuntimeException("User not found"));
        String encodedPwd = passwordEncoder.encode(pwd);
        user.changePassword(encodedPwd);
        userRepository.save(user);
    }

    /**
     * JWT 토큰 재발급
     */
    public Tokens refreshTokens(Authentication authentication) throws JsonProcessingException{
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
        TODO 작성한 문의내역 삭제??
        for(UserInquiries inquiries : user.getUserInquiries()){
            inquiries.signoutWriter();
        }*/
        userRepository.delete(user);
    }
}
