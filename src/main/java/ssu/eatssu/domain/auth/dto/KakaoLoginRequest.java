package ssu.eatssu.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ssu.eatssu.global.log.annotation.LogMask;

@Schema(title = "카카오 로그인 및 회원가입")
public record KakaoLoginRequest(
        @LogMask
        @NotBlank(message = "이메일을 입력해주세요.")
        @Email(message = "올바른 이메일 주소를 입력해주세요.")
        @Schema(description = "이메일", example = "test@email.com")
        String email,

        @LogMask
        @Schema(description = "providerId", example = "10378247832195")
        String providerId
) {

}
