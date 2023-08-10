package ssu.eatssu.web.oauth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "애플 로그인 및 회원가입")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleLogin {
    @Schema(description = "identityToken", example = "eyJraWQiOiJXNldjT0tCIiwiYWxnIjoi...")
    private String identityToken;
}
