package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import ssu.eatssu.domain.user.entity.Language;

@Schema(title = "언어 설정 수정")
public record LanguageUpdateRequest(
        @NotNull(message = "언어 설정을 입력해주세요.")
        @Schema(description = "언어 설정", example = "EN", allowableValues = {"KO", "EN", "JA", "VI"})
        Language language) {
}
