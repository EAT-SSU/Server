package ssu.eatssu.web.restaurant.dto;

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
        private Double mainGrade;

        @Schema(description = "식단 속 메뉴 리스트")
        private List<ChangeMenuInfo> changeMenuInfoList;

        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        private static class ChangeMenuInfo {
            @Schema(description = "메뉴 식별자", example = "2")
            private Long menuId;

            @Schema(description = "메뉴 이름", example = "돈까스")
            private String name;

        }

        public static TodayMeal from (Meal meal){
            if(!meal.getMealMenus().isEmpty()){
                List<MealMenu> mealMenuList = meal.getMealMenus();
                List<ChangeMenuInfo> changeMenuList = new ArrayList<>();
                for(MealMenu mealMenu : mealMenuList){
                    Menu menu = mealMenu.getMenu();
                    ChangeMenuInfo changeMenuInfo = new ChangeMenuInfo(menu.getId(), menu.getName());
                    changeMenuList.add(changeMenuInfo);
                }
                return new TodayMeal(meal.getId(), meal.getRestaurant().getRestaurantName().getPrice()
                        , meal.getGradeMap().get("mainGrade"), changeMenuList);
            }else{
                return null;
            }
        }

    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FixMenuList {
        private List<FixMenuInfo> fixMenuInfoList;
        @Getter
        @NoArgsConstructor
        @AllArgsConstructor
        private static class FixMenuInfo{
            @Schema(description = "메뉴 식별자", example = "2")
            private Long menuId;

            @Schema(description = "메뉴 이름", example = "돈까스")
            private String name;

            @Schema(description = "메뉴 평점(평점이 없으면 null)", example = "4.4")
            private Double mainGrade;

            @Schema(description = "가격", example = "5000")
            private Integer price;
        }

        public static FixMenuList from(List<Menu> menus){
            List<FixMenuInfo> fixMenuList = new ArrayList<>();
            for (Menu menu : menus) {
                FixMenuInfo menuInfo = new FixMenuInfo(menu.getId(), menu.getName(), menu.getMainGrade(), menu.getPrice());
                fixMenuList.add(menuInfo);
            }
            return new FixMenuList(fixMenuList);
        }
    }

}
