package ssu.eatssu.domain.menu.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "식단 속 메뉴 정보 리스트")
public class MenusInMealResponse {

    @Schema(description = "식단 속 메뉴 목록", example = "[]")
    private List<BriefMenuResponse> briefMenus;

    public static MenusInMealResponse from(List<Menu> menus, Map<String, String> mainMenuTranslations) {
        List<BriefMenuResponse> menusInformation = menus.stream()
                                                       .map(menu -> new BriefMenuResponse(menu, mainMenuTranslations))
                                                       .toList();

        return new MenusInMealResponse(menusInformation);
    }
}
