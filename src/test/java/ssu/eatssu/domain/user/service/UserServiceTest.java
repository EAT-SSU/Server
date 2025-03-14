package ssu.eatssu.domain.user.service;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.dto.NicknameUpdateRequest;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UserServiceTest {

	@Autowired
	private UserService userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ReviewRepository reviewRepository;

	@BeforeEach
	void setUp() {
		reviewRepository.deleteAll();
		userRepository.deleteAll();
	}

	@Test
	void 회원가입을_한다() {
		// given & when
		회원가입_요청();

		// then
		assertThat(userRepository.findAll()).hasSize(1);
	}

	@Test
	void 닉네임을_변경한다() {
		// given
		User user = 회원가입_요청();
		CustomUserDetails userDetails = UserDetails_생성(user);

		// when
		userService.updateNickname(userDetails, new NicknameUpdateRequest("newNickname"));

		// then
		user = userRepository.findById(userDetails.getId()).orElseThrow();
		assertThat(user.getNickname()).isEqualTo("newNickname");
	}

	@Test
	void 회원탈퇴를_한다() {
		// given
		User user = 회원가입_요청();
		CustomUserDetails userDetails = UserDetails_생성(user);

		// when
		userService.withdraw(userDetails);

		// then
		assertThat(userRepository.findAll()).hasSize(0);
	}

	@Test
	void 중복_이메일을_확인한다() {
		// given
		User user = 회원가입_요청();

		// when
		boolean isDuplicated = userService.validateDuplicatedEmail(user.getEmail());

		// then
		assertThat(isDuplicated).isFalse();
	}

	private User 회원가입_요청() {
		return userService.join("test@test.com", OAuthProvider.EATSSU, "1234");
	}

	@NotNull
	private CustomUserDetails UserDetails_생성(User user) {
		return new CustomUserDetails(user);
	}
}
