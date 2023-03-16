package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.UserRepository;
import ssu.eatssu.web.user.dto.Join;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public void join(Join join){
        join.setPwd(passwordEncoder.encode(join.getPwd()));
        User user = User.join(join.getEmail(), join.getPwd(), join.getNickname());
        userRepository.save(user);
    }


    public void login(String email, String pwd) {
        //유저 존재 여부 체크
        userRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("User not found"));

        // 1. ID/pwd 를 기반으로 Authentication 객체 생성
        //    이때 authentication은 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, pwd);

        // 2. 실제 검증 (사용자 비밀번호 체크)
        //    authenticate 메서드 실행 => CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 3. 인증 정보를 바탕으로 JWT 토큰 생성
            /*TokenDto jwt = tokenProvider.createToken(authentication);
            return jwt;*/

    }
}
