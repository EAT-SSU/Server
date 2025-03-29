package ssu.eatssu.domain.menu.presentation.dto.response;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "식단 속 메뉴 정보 리스트")
public class MenusInMealResponse {

	@Schema(description = "식단 속 메뉴 목록", example = "[]")
	private List<BriefMenuResponse> briefMenus;

	public static MenusInMealResponse from(Meal meal) {
		List<BriefMenuResponse> menusInformation = meal.getMealMenus().stream()
													   .map(MealMenu::getMenu)
													   .map(BriefMenuResponse::new)
													   .toList();

		return new MenusInMealResponse(menusInformation);
	}
}
