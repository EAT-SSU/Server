package ssu.eatssu.domain.user.service;

import static org.assertj.core.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.auth.entity.CustomUserDetails;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.dto.NicknameUpdateRequest;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void 회원가입을_한다() {
        // given & when
        userService.join("test@test.com", OAuthProvider.EATSSU, "1234");

        // then
        assertThat(userRepository.findAll()).hasSize(1);
    }

}