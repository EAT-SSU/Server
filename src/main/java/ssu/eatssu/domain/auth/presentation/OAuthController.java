package ssu.eatssu.domain.auth.presentation;

import static ssu.eatssu.domain.auth.infrastructure.SecurityUtil.*;

import java.net.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.auth.service.OAuthService;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.global.handler.response.BaseResponse;

@Slf4j
@RestController
@RequestMapping("/oauths")
@RequiredArgsConstructor
@Tag(name = "Oauth", description = "Oauth API")
public class OAuthController {

	private final OAuthService oauthService;

	@Operation(summary = "카카오 회원가입, 로그인 [인증 토큰 필요 X]", description = """
		카카오 회원가입, 로그인 API 입니다.<br><br>
		가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
		""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "카카오 회원가입/로그인 성공")
	})
	@PostMapping("/kakao")
	public BaseResponse<Tokens> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
		Tokens tokens = oauthService.kakaoLogin(request);
		return BaseResponse.success(tokens);
	}

	@Operation(summary = "애플 회원가입, 로그인 [인증 토큰 필요 X]", description = """
		애플 로그인, 회원가입 API 입니다.<br><br>
		가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
		""")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "애플 회원가입/로그인 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/apple")
	public BaseResponse<Tokens> appleLogin(@Valid @RequestBody AppleLoginRequest request) {
		Tokens tokens = oauthService.appleLogin(request);
		return BaseResponse.success(tokens);
	}

	@Operation(summary = "토큰 재발급", description = "accessToken, refreshToken 재발급 API 입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
	})
	@PostMapping("/reissue/token")
	public BaseResponse<Tokens> refreshToken() {
		Tokens tokens = oauthService.refreshTokens(getLoginUser());
		return BaseResponse.success(tokens);
	}

	@Operation(summary = "유효한 토큰 확인", description = "해당 토큰이 유효하면 true 반환하는, 유효하지 않은 false 반환하는 API 입니다")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "유효한 토큰인지 확인 성공")
	})
	@GetMapping("/valid/token")
	public BaseResponse<Boolean> validToken(@Valid @RequestBody ValidRequest request) {
		Boolean response = oauthService.validToken(request);
		return BaseResponse.success(response);
	}

}
