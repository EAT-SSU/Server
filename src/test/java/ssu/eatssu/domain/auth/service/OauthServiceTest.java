package ssu.eatssu.domain.auth.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.entity.JwtTokenProvider;
import ssu.eatssu.domain.auth.infrastructure.TestAppleAuthenticator;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;

@SpringBootTest
class OauthServiceTest {

    @Autowired
    private OAuthService oauthService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;


    @BeforeEach
    void setUp() {
        oauthService = new OAuthService(userService, userRepository,
            new TestAppleAuthenticator(), authenticationManagerBuilder, jwtTokenProvider);
        userRepository.deleteAll();
    }

    @Test
    void 카카오로_회원가입을_한다() {
        // given
        KakaoLoginRequest request = new KakaoLoginRequest("test@email.com", "10378247832195");

        // when
        oauthService.kakaoLogin(request);

        // then
        User user = userRepository.findByProviderId(request.providerId()).get();
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getProviderId()).isEqualTo(request.providerId());
    }

    @Test
    void 애플로_회원가입을_한다() {
        // given
        AppleLoginRequest request = new AppleLoginRequest("eyJraWQiOiJXNldjT0tCIiwiYWxnIjoi");

        // when
        oauthService.appleLogin(request);

        // then
        User user = userRepository.findByProviderId("1234567890").get();
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(user.getEmail()).isEqualTo("test@test.com");
        assertThat(user.getProviderId()).isEqualTo("1234567890");
    }
}