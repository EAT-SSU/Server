package ssu.eatssu.web.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "카카오 로그인 및 회원가입")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoLogin {
    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @Schema(description = "이메일", example = "test@email.com")
    private String email;

    @Schema(description = "providerId", example = "10378247832195")
    private String providerId;

}
