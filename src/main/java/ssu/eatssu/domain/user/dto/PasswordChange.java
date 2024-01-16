package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "비밀번호 변경")
@NoArgsConstructor
@Getter
public class PasswordChange {

    @NotBlank(message = "변경할 비밀번호를 입력해주세요.")
    @Schema(description = "변경할 비밀번호", example = "password1234")
    private String pwd; //8자 이상 13자 이하(영숫특)
}
