package ssu.eatssu.web.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.OauthService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.oauth.dto.AppleLogin;
import ssu.eatssu.web.oauth.dto.KakaoLogin;
import ssu.eatssu.web.user.dto.*;

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
    public ResponseEntity<Tokens> kakaoLogin(@Valid @RequestBody KakaoLogin login) throws JsonProcessingException {
        Tokens tokens = oauthService.loginByKakao(login.getEmail(), login.getProviderId());
        return ResponseEntity.ok(tokens);
    }

    /**
     * 애플 로그인, 회원가입
     */
    @Operation(summary = "애플 로그인, 회원가입", description = "애플 로그인, 회원가입")
    @PostMapping("/apple")
    public ResponseEntity<Tokens> appleLogin(@Valid @RequestBody AppleLogin login) throws NoSuchAlgorithmException, InvalidKeySpecException {
        Tokens tokens = oauthService.loginByApple(login.getIdentityToken());
        return ResponseEntity.ok(tokens);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }


}
