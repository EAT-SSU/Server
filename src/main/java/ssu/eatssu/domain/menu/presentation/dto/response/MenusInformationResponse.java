package ssu.eatssu.domain.menu.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.presentation.dto.BriefMenuResponse;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="식단 속 메뉴 정보 리스트")
public class MenusInformationResponse {

    @Schema(description = "식단 속 메뉴 목록", example = "[]")
    private List<BriefMenuResponse> menusInformation;

    public static MenusInformationResponse from(Meal meal) {
        if (!meal.getMealMenus().isEmpty()) {
            List<BriefMenuResponse> menusInformation = meal.getMealMenus().stream()
                    .map(MealMenu::getMenu)
                    .map(BriefMenuResponse::new).toList();
            return new MenusInformationResponse(menusInformation);
        } else {
            return null;
        }
    }
}