package ssu.eatssu.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.global.log.annotation.LogMask;

@Schema(title = "애플 로그인 및 회원가입")
public record AppleLoginRequest(
        @LogMask
        @Schema(description = "identityToken", example = "eyJraWQiOiJXNldjT0tCIiwiYWxnIjoi...")
        String identityToken
) {

}
