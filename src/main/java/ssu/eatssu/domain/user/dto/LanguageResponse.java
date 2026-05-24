package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;

@Schema(title = "언어 설정 조회")
public record LanguageResponse(
        @Schema(description = "언어 설정", example = "KO")
        Language language) {

    public static LanguageResponse from(User user) {
        return new LanguageResponse(user.getLanguage());
    }
}
