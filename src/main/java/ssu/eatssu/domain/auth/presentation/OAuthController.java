package ssu.eatssu.domain.auth.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.dto.request.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.request.AppleLoginRequestV2;
import ssu.eatssu.domain.auth.dto.request.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.request.KakaoLoginRequestV2;
import ssu.eatssu.domain.auth.dto.request.ValidRequest;
import ssu.eatssu.domain.auth.presentation.docs.OAuthControllerDocs;
import ssu.eatssu.domain.auth.service.OAuthService;
import ssu.eatssu.domain.user.dto.response.Tokens;
import ssu.eatssu.global.handler.response.BaseResponse;

import static ssu.eatssu.domain.auth.infrastructure.SecurityUtil.getLoginUser;

@Slf4j
@RestController
@RequestMapping("/oauths")
@RequiredArgsConstructor
public class OAuthController implements OAuthControllerDocs {

    private final OAuthService oauthService;

    // TODO : 로그인 & 회원 가입 마이그레이션 이후에 지울 것.
    @Override
    @PostMapping("/kakao")
    public BaseResponse<Tokens> kakaoLogin(@Valid @RequestBody KakaoLoginRequest request) {
        long startTime = System.currentTimeMillis();
        Tokens tokens = oauthService.kakaoLogin(request);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("OAuthWarmupRunner 완료 - 소요 시간: {} ms", duration);

        return BaseResponse.success(tokens);
    }

    @Override
    @PostMapping("/v2/kakao")
    public BaseResponse<Tokens> kakaoLoginV2(@Valid @RequestBody KakaoLoginRequestV2 request) {
        long startTime = System.currentTimeMillis();
        Tokens tokens = oauthService.kakaoLoginV2(request);
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        log.info("OAuthWarmupRunner 완료 - 소요 시간: {} ms", duration);

        return BaseResponse.success(tokens);
    }

    // TODO : 로그인 & 회원 가입 마이그레이션 이후에 지울 것.
    @Override
    @PostMapping("/apple")
    public BaseResponse<Tokens> appleLogin(@Valid @RequestBody AppleLoginRequest request) {
        Tokens tokens = oauthService.appleLogin(request);
        return BaseResponse.success(tokens);
    }

    @Override
    @PostMapping("/v2/apple")
    public BaseResponse<Tokens> appleLoginV2(@Valid @RequestBody AppleLoginRequestV2 request) {
        Tokens tokens = oauthService.appleLoginV2(request);
        return BaseResponse.success(tokens);
    }

    @Override
    @PostMapping("/reissue/token")
    public BaseResponse<Tokens> refreshToken() {
        Tokens tokens = oauthService.refreshTokens(getLoginUser());
        return BaseResponse.success(tokens);
    }

    @Override
    @PostMapping("/valid/token")
    public BaseResponse<Boolean> validToken(@Valid @RequestBody ValidRequest request) {
        Boolean response = oauthService.validToken(request);
        return BaseResponse.success(response);
    }

}
