package ssu.eatssu.domain.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;

public class MenuResponse {

    private MenuResponse() {
    }

    @Schema(title = "오늘의 식단 리스트 조회 Res")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MealInformationResponse {

        @Schema(description = "식단 식별자 mealId", example = "12")
        private Long mealId;

        @Schema(description = "가격", example = "5000")
        private Integer price;

        @Schema(description = "식단 평점(평점이 없으면 null)", example = "4.4")
        private Double mainRating;

        @Schema(description = "식단 속 메뉴 정보 리스트")
        private List<BriefMenuInformation> menusInformation;


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

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuInformationResponse {

        @Schema(description = "메뉴 식별자", example = "2")
        private Long menuId;

        @Schema(description = "메뉴 이름", example = "돈까스")
        private String name;

        @Schema(description = "가격", example = "5000")
        private Integer price;

        @Schema(description = "메뉴 평점(평점이 없으면 null)", example = "4.4")
        private Double mainRating;


        public static MenuInformationResponse from(Menu menu) {
            return new MenuInformationResponse(menu.getId(), menu.getName(),
	menu.getPrice(), menu.getReviews().getAverageMainRating());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenusInformationResponse {

        private List<BriefMenuInformation> menusInformation;

        public static MenusInformationResponse from(Meal meal) {
            if (!meal.getMealMenus().isEmpty()) {
	List<BriefMenuInformation> menusInformation = meal.getMealMenus().stream()
	    .map(MealMenu::getMenu)
	    .map(BriefMenuInformation::new).toList();
	return new MenusInformationResponse(menusInformation);
            } else {
	return null;
            }
        }
    }

    @Getter
    @NoArgsConstructor
    private static class BriefMenuInformation {

        @Schema(description = "메뉴 식별자", example = "2")
        private Long menuId;

        @Schema(description = "메뉴 이름", example = "돈까스")
        private String name;

        private BriefMenuInformation(Menu menu) {
            this.menuId = menu.getId();
            this.name = menu.getName();
        }
    }
}
