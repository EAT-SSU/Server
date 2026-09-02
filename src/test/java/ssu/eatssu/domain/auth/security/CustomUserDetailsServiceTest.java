package ssu.eatssu.domain.auth.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void loadUserByUsernameReturnsUserWhenEmailExists() {
        // given
        userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));

        // when
        UserDetails userDetails = customUserDetailsService.loadUserByUsername("test@test.com");

        // then
        assertThat(userDetails.getUsername()).isEqualTo("test@test.com");
    }

    @Test
    void loadUserByUsernameThrowsWhenEmailDoesNotExist() {
        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing@test.com"))
                .isInstanceOf(BaseException.class);
    }
}
