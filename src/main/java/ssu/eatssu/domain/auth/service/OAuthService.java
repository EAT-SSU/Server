package ssu.eatssu.domain.auth.service;

import static ssu.eatssu.domain.auth.entity.OAuthProvider.*;
import static ssu.eatssu.domain.auth.security.JwtAuthenticationFilter.*;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.OAuthInfo;
import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.auth.entity.AppleAuthenticator;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.global.handler.response.BaseException;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OAuthService {

	private final UserService userService;
	private final UserRepository userRepository;
	private final AppleAuthenticator appleAuthenticator;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
	private final JwtTokenProvider jwtTokenProvider;

	private static final String AUTHORIZATION_HEADER = "Authorization";

	private static final String BEARER_TYPE = "Bearer";

	public Tokens kakaoLogin(KakaoLoginRequest request) {
		User user = userRepository.findByProviderId(request.providerId())
								  .orElseGet(
									  () -> userService.join(request.email(), KAKAO, request.providerId()));

		return generateOauthJwtTokens(user.getEmail(), KAKAO,
			request.providerId());
	}

	public Tokens appleLogin(AppleLoginRequest request) {
		OAuthInfo oAuthInfo = appleAuthenticator.getOAuthInfoByIdentityToken(
			request.identityToken());

		User user = userRepository.findByProviderId(oAuthInfo.providerId())
								  .orElseGet(() -> userService.join(oAuthInfo.email(), APPLE,
									  oAuthInfo.providerId()));

		//todo 이메일 갱신의 이유?
		updateAppleUserEmail(user, oAuthInfo.email());

		return generateOauthJwtTokens(user.getEmail(), APPLE,
			oAuthInfo.providerId());
	}

	/**
	 * JWT 토큰 재발급
	 */
	public Tokens refreshTokens(Authentication authentication) {
		return jwtTokenProvider.generateTokens(authentication);
	}

	private void updateAppleUserEmail(User user, String email) {
		if (isHideEmail(user.getEmail()) && !isHideEmail(email)) {
			user.updateEmail(email);
			userRepository.save(user);
		}
	}

	/**
	 * 유효한 토큰인지 확인
	 */

	public Boolean validToken(ValidRequest request){
		String token = request.token();

		try {
			return jwtTokenProvider.validateToken(token);
		} catch (BaseException e) {
			log.warn("토큰 유효성 검사 중 예외 발생: {}", e.getMessage());
			return false;
		}
	}

	private boolean isHideEmail(String email) {
		if (email.length() > 25) {
			return email.startsWith("@privaterelay.appleid.com", email.length() - 25);
		} else {
			return false;
		}
	}

	/**
	 * Oauth 회원 - email, providerId 를 통해 JwtToken 을 생성
	 * todo: 같은 이메일로 카카오, 애플 등 여러 회원가입을 한 회원 처리 필요
	 */
	private Tokens generateOauthJwtTokens(String email, OAuthProvider provider, String providerId) {
		// email, credentials 를 기반으로 Authentication 객체 생성
		// 이때 authentication 은 인증 여부를 확인하는 authenticated 값이 false
		UsernamePasswordAuthenticationToken authenticationToken =
			new UsernamePasswordAuthenticationToken(email,
				makeOauthCredentials(provider, providerId));

		// 실제 검증 (사용자 비밀번호 체크)
		// authenticate 메서드 실행 => CustomUserDetailsService 에서 만든 loadUserByUsername 메서드 실행
		Authentication authentication = authenticationManagerBuilder.getObject()
																	.authenticate(authenticationToken);

		// 인증 정보를 바탕으로 JWT 토큰 생성
		return jwtTokenProvider.generateTokens(authentication);
	}

	private String makeOauthCredentials(OAuthProvider provider, String providerId) {
		return provider + providerId;
	}

}
