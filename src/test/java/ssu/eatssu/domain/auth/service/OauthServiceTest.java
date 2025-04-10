package ssu.eatssu.domain.auth.service;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.auth.service.OAuthService;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;

@ExtendWith(MockitoExtension.class)
class OauthServiceTest {

	@InjectMocks
	private OAuthService oauthService;

	@Mock
	private JwtTokenProvider jwtTokenProvider;

	@Test
	void 토큰_유효성_검사() {
		// given
		String token = "eyJraWQiOiJXNldjT0tCIiwiYWxnIjoi";
		ValidRequest request = new ValidRequest(token);
		Mockito.when(jwtTokenProvider.validateToken(token)).thenReturn(true);

		// when
		Boolean result = oauthService.validToken(request);

		// then
		assertTrue(result);
	}
}
