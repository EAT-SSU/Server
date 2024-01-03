package ssu.eatssu.web.oauth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponse;
import ssu.eatssu.service.OauthService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.oauth.dto.AppleLogin;
import ssu.eatssu.web.oauth.dto.KakaoLogin;
import ssu.eatssu.web.user.dto.Tokens;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@Slf4j
@RestController
@RequestMapping("/oauth")
@RequiredArgsConstructor
@Tag(name="Oauth",description = "Oauth API")
public class OauthController {

    private final OauthService oauthService;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    /**
     * 카카오 회원가입, 로그인
     */
    @Operation(summary = "카카오 회원가입, 로그인", description = """
            카카오 회원가입, 로그인 API 입니다.<br><br>
            가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 회원가입/로그인 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/kakao")
    public BaseResponse<Tokens> kakaoLogin(@Valid @RequestBody KakaoLogin login) {
        Tokens tokens = oauthService.kakaoLogin(login.getEmail(), login.getProviderId());
        return BaseResponse.success(tokens);
    }

    /**
     * 애플 회원가입, 로그인
     */
    @Operation(summary = "애플 회원가입, 로그인", description = """
            애플 로그인, 회원가입 API 입니다.<br><br>
            가입된 회원일 경우 카카오 로그인, 미가입 회원일 경우 회원가입 후 자동 로그인됩니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "애플 회원가입/로그인 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/apple")
    public BaseResponse<Tokens> appleLogin(@Valid @RequestBody AppleLogin login) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        Tokens tokens = oauthService.appleLogin(login.getIdentityToken());
        return BaseResponse.success(tokens);
    }

}
