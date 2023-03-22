package ssu.eatssu.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.jwt.JwtTokenProvider;
import ssu.eatssu.web.user.dto.Tokens;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public Tokens join(String email, String pwd, String nickname) throws JsonProcessingException{
        String encodedPwd = passwordEncoder.encode(pwd);
        User user = User.join(email, encodedPwd, nickname);
        userRepository.save(user);

        return generateJwtTokens(email, pwd);
    }

    public Tokens login(String email, String pwd) throws JsonProcessingException {
        //유저 존재 여부 체크
        userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        return generateJwtTokens(email, pwd);
    }

    public void updateNickname(Long userId, String nickname) {
        User user = userRepository.findById(userId)
              .orElseThrow(()-> new RuntimeException("User not found"));
        user.updateNickname(nickname);
        userRepository.save(user);
    }

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

    public void changePassword(Long userId, String pwd) {
        User user = userRepository.findById(userId)
              .orElseThrow(()-> new RuntimeException("User not found"));
        String encodedPwd = passwordEncoder.encode(pwd);
        user.changePassword(encodedPwd);
        userRepository.save(user);
    }

    public Tokens refreshAccessToken(Authentication authentication) throws JsonProcessingException{

        return jwtTokenProvider.generateTokens(authentication);
    }
}
