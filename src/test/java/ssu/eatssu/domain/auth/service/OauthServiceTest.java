package ssu.eatssu.domain.auth.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

@SpringBootTest
class OauthServiceTest {

    @Autowired
    private OauthService oauthService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void 카카오로_회원가입을_한다() {
        // given
        KakaoLoginRequest request = new KakaoLoginRequest("test@email.com", "10378247832195");

        // when
        oauthService.kakaoLogin(request);
        User user = userRepository.findByProviderId(request.providerId()).get();

        // then
        assertThat(userRepository.findAll()).hasSize(1);
        assertThat(user.getEmail()).isEqualTo(request.email());
        assertThat(user.getProviderId()).isEqualTo(request.providerId());
    }

}