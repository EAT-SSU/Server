package ssu.eatssu.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
