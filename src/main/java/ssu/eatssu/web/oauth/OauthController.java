package ssu.eatssu.web.oauth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseResponse;
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
     * 카카오 로그인, 회원가입
     */
    @Operation(summary = "카카오 로그인, 회원가입", description = "카카오 로그인, 회원가입")
    @PostMapping("/kakao")
    public BaseResponse<Tokens> kakaoLogin(@Valid @RequestBody KakaoLogin login) {
        Tokens tokens = oauthService.kakaoLogin(login.getEmail(), login.getProviderId());
        return BaseResponse.success(tokens);
    }

    /**
     * 애플 로그인, 회원가입
     */
    @Operation(summary = "애플 로그인, 회원가입", description = "애플 로그인, 회원가입")
    @PostMapping("/apple")
    public BaseResponse<Tokens> appleLogin(@Valid @RequestBody AppleLogin login) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        Tokens tokens = oauthService.appleLogin(login.getIdentityToken());
        return BaseResponse.success(tokens);
    }

}
