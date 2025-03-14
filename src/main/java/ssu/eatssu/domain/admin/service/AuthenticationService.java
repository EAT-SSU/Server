package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ssu.eatssu.domain.admin.controller.AdminAuth;
import ssu.eatssu.domain.admin.dto.LoginRequest;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

@RequiredArgsConstructor
@Service
public class AuthenticationService {
	private final JwtTokenProvider tokenProvider;
	private final PasswordEncoder passwordEncoder;
	private final UserRepository userRepository;
	private final AdminAuth adminAuth;

	public Tokens login(LoginRequest request) {
		return tokenProvider.generateTokens(request.loginId(), request.password());
	}

	private void join(String loginId, String password) {
		String credentials = createCredentials(password);

		//회원가입
		User user = User.adminJoin(loginId, credentials);
		userRepository.save(user);
	}

	private String createCredentials(String password) {
		return passwordEncoder.encode(password);
	}
}
