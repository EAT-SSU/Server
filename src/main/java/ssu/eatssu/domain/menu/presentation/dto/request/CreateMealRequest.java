package ssu.eatssu.domain.menu.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(title = "식단 추가")
public record CreateMealRequest(
        @Schema(description = "메뉴명 리스트", example = "[\"돈까스\", \"샐러드\", \"김치\"]")
        List<String> menuNames,
        @Schema(description = "대표메뉴 목록(영어 UI 노출용, optional)")
        List<MainMenuRequest> mainMenus
) {

}
