package ssu.eatssu.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.repository.UserRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("회원 가입 테스트")
    void join() {
        //given
        User user = User.join("email1", "pwd1", "nickname1");

        //when
        userRepository.save(user);

        //then
        Optional<User> findUser = userRepository.findByEmail(user.getEmail());
        assertThat(findUser.isPresent()).isTrue();
        assertThat(findUser.get()).isEqualTo(user);

    }
}