package ssu.eatssu.domain.auth.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.dto.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.AppleLoginRequestV2;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.KakaoLoginRequestV2;
import ssu.eatssu.domain.auth.dto.ValidRequest;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.global.handler.response.BaseResponse;

@Tag(name = "Oauth", description = "Oauth API")
public interface OAuthControllerDocs {

    @Operation(summary = "카카오 회원가입, 로그인 [인증 토큰 필요 X]", description = """
            카카오 회원가입, 로그인 API 입니다.<br><br>
            가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 회원가입/로그인 성공")
    })
    BaseResponse<Tokens> kakaoLogin(KakaoLoginRequest request);

    @Operation(summary = "카카오 회원가입, 로그인 V2 [인증 토큰 필요 X]", description = """
            카카오 회원가입, 로그인 V2 API 입니다.<br><br>
            가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 회원가입/로그인 성공")
    })
    BaseResponse<Tokens> kakaoLoginV2(KakaoLoginRequestV2 request);

    @Operation(summary = "애플 회원가입, 로그인 [인증 토큰 필요 X]", description = """
            애플 로그인, 회원가입 API 입니다.<br><br>
            가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "애플 회원가입/로그인 성공")
    })
    BaseResponse<Tokens> appleLogin(AppleLoginRequest request);

    @Operation(summary = "애플 회원가입, 로그인 V2 [인증 토큰 필요 X]", description = """
            애플 로그인, 회원가입 API V2 입니다.<br><br>
            가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "애플 회원가입/로그인 성공")
    })
    BaseResponse<Tokens> appleLoginV2(AppleLoginRequestV2 request);

    @Operation(summary = "토큰 재발급", description = "accessToken, refreshToken 재발급 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    })
    BaseResponse<Tokens> refreshToken();

    @Operation(summary = "유효한 토큰 확인 [인증 토큰 필요 X]", description = "해당 토큰이 유효하면 true 반환하는, 유효하지 않은 false 반환하는 API 입니다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효한 토큰인지 확인 성공")
    })
    BaseResponse<Boolean> validToken(ValidRequest request);
}
