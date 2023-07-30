package ssu.eatssu.web.oauth;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.service.OauthService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.oauth.dto.KakaoLogin;
import ssu.eatssu.web.user.dto.*;

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


}
