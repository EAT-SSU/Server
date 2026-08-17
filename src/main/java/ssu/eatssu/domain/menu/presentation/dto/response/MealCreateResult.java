package ssu.eatssu.domain.menu.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "식단 추가 결과")
public record MealCreateResult(
        @Schema(description = "식단 식별자", example = "1234")
        Long mealId,
        @Schema(description = "menuNames와 매칭되지 않아 저장되지 못한 mainMenus.nameKo 목록", example = "[]")
        List<String> unmatchedMainMenus
) {

}
