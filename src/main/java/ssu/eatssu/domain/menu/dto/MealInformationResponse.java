package ssu.eatssu.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "오늘의 식단 리스트 조회 Res")
public class MealInformationResponse {

    @Schema(description = "식단 식별자 mealId", example = "12")
    private Long mealId;

    @Schema(description = "가격", example = "5000")
    private Integer price;

    @Schema(description = "식단 평점(평점이 없으면 null)", example = "4.4")
    private Double mainRating;

    @Schema(description = "식단 속 메뉴 정보 리스트")
    private List<BriefMenuInformation> menusInformationList;


    public static MealInformationResponse from(Meal meal) {
        if (!meal.getMealMenus().isEmpty()) {
            List<BriefMenuInformation> menusInformation = meal.getMealMenus().stream()
                    .map(MealMenu::getMenu)
                    .map(BriefMenuInformation::new).toList();

            return new MealInformationResponse(meal.getId(),
                    meal.getRestaurant().getRestaurantPrice(),
                    meal.getAverateMainRating(), menusInformation);
        } else {
            return null;
        }
    }
}


