package ssu.eatssu.web.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ssu.eatssu.domain.Meal;
import ssu.eatssu.domain.MealMenu;
import ssu.eatssu.domain.Menu;

import java.util.ArrayList;
import java.util.List;

public class MenuResDto {

    private MenuResDto() {
    }

    @Schema(title = "오늘의 식단 리스트 조회 Res")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TodayMeal {
        @Schema(description = "식단 식별자 mealId", example = "12")
        private Long mealId;

        @Schema(description = "가격", example = "5000")
        private Integer price;

        @Schema(description = "식단 평점(평점이 없으면 null)", example = "4.4")
        private Double mainRate;

        @Schema(description = "식단 속 메뉴 정보 리스트")
        private List<MenuInfo> menuInfoList;

        public static TodayMeal from(Meal meal, Double mainRate) {
            if (!meal.getMealMenus().isEmpty()) {
                List<MenuInfo> menuInfoList = meal.getMealMenus().stream()
                        .map(MealMenu::getMenu)
                        .map(MenuInfo::new).toList();

                return new TodayMeal(meal.getId(), meal.getRestaurant().getRestaurantName().getPrice()
                        , mainRate, menuInfoList);
            } else {
                return null;
            }
        }

        @Getter
        @NoArgsConstructor
        private static class MenuInfo {
            @Schema(description = "메뉴 식별자", example = "2")
            private Long menuId;

            @Schema(description = "메뉴 이름", example = "돈까스")
            private String name;

            private MenuInfo(Menu menu) {
                this.menuId = menu.getId();
                this.name = menu.getName();
            }

        }

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixMenuInfo {

        @Schema(description = "메뉴 식별자", example = "2")
        private Long menuId;

        @Schema(description = "메뉴 이름", example = "돈까스")
        private String name;

        @Schema(description = "메뉴 평점(평점이 없으면 null)", example = "4.4")
        private Double mainRate;

        @Schema(description = "가격", example = "5000")
        private Integer price;

        public static FixMenuInfo from(Menu menu, Double mainRate) {
            return new FixMenuInfo(menu.getId(), menu.getName(), mainRate, menu.getPrice());
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MenuList {
        private List<MenuInfo> menuInfoList;

        public static MenuList from(Meal meal) {
            if (!meal.getMealMenus().isEmpty()) {
                List<MenuInfo> menuInfoList = new ArrayList<>();
                for (MealMenu mealMenu : meal.getMealMenus()) {
                    Menu menu = mealMenu.getMenu();
                    MenuInfo menuInfo = new MenuInfo(menu.getId(), menu.getName());
                    menuInfoList.add(menuInfo);
                }
                return new MenuList(menuInfoList);
            } else {
                return null;
            }
        }

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        private static class MenuInfo {
            @Schema(description = "메뉴 식별자", example = "2")
            private Long menuId;

            @Schema(description = "메뉴 이름", example = "돈까스")
            private String name;
        }
    }

}
