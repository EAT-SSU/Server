package ssu.eatssu.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.OauthProvider;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.jwt.JwtTokenProvider;
import ssu.eatssu.web.user.dto.Tokens;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class OauthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;


    public Tokens loginByKakao(String email, String providerId) throws JsonProcessingException {
        Optional<User> user = userRepository.findByEmail(email);
        String pwd = makePassword(OauthProvider.KAKAO, providerId);
        if (user.isEmpty()) {
            join(email, providerId, OauthProvider.KAKAO);
        }
        return generateJwtTokens(email, pwd);
    }
    private void join(String email, String providerId, OauthProvider provider) throws JsonProcessingException {
        String pwd = makePassword(provider, providerId);
        String encodedPwd = passwordEncoder.encode(pwd);
        User user = User.Oauthjoin(email, encodedPwd, provider.toString()+"유저", provider, providerId);
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

    private String makePassword(OauthProvider provider, String providerId){
        return provider.toString()+providerId;
    }

}
