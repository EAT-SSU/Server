package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(title = "닉네임 수정")
public record NicknameUpdateRequest(
        @NotBlank(message = "닉네임을 입력해주세요.") @Schema(description = "닉네임", example = "jumukzzang") String nickname) {

}
