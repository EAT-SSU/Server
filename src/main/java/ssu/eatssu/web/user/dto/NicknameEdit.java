package ssu.eatssu.web.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(title = "닉네임 수정")
@NoArgsConstructor
@Getter
public class NicknameEdit {

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Schema(description = "닉네임", example = "jumukzzang")
    private String nickname;
}
