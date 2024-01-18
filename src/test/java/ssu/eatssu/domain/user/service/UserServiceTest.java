package ssu.eatssu.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.user.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;

    @Test
    void 회원_탈퇴_성공시_참을_반환한다() {
        // given

        // when
        // then
        Assertions.assertThat(userRepository.findAll()).hasSize(0);

    }

}