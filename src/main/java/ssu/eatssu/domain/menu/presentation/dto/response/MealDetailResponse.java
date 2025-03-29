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
@Schema(title = "오늘의 식단 리스트 조회 Response")
public class MealDetailResponse {

	@Schema(description = "식단 식별자 mealId", example = "12")
	private Long mealId;

	@Schema(description = "가격", example = "5000")
	private Integer price;

	@Schema(description = "식단 평점(평점이 없으면 null)", example = "4.4")
	private Double rating;

	@Schema(description = "식단 속 메뉴 정보 리스트")
	private List<BriefMenuResponse> briefMenus;

	public static MealDetailResponse from(Meal meal, Double mainRating) {
		List<BriefMenuResponse> briefMenus = meal.getMealMenus().stream()
												 .map(MealMenu::getMenu)
												 .map(BriefMenuResponse::new)
												 .toList();

		return new MealDetailResponse(
			meal.getId(),
			meal.getPrice(),
			mainRating,
			briefMenus
		);
	}
}


