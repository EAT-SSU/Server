package ssu.eatssu.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "로그인")
@NoArgsConstructor
@Getter
public class Login {

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 주소를 입력해주세요.")
    @Schema(description = "이메일", example = "test@email.com")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Schema(description = "비밀번호", example = "qlalfqjsgh")
    private String pwd; //8자 이상 13자 이하(영숫특)
}
