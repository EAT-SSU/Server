package ssu.eatssu.web.restaurant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.Menu;

import java.util.ArrayList;
import java.util.List;

@Schema(title = "식단 조회")
@Getter
@AllArgsConstructor
public class TodayMenu {
    private List<MenuInfo> menuInfoList;

    @AllArgsConstructor
    @Getter
    private static class MenuInfo{

        @Schema(description = "메뉴 식별자", example = "2")
        private Long menuId;

        @Schema(description = "메뉴 이름", example = "돈까스")
        private String name;
        @Schema(description = "메뉴 가격", example = "5000")
        private Integer price;

        @Schema(description = "메뉴 평점(평점이 없으면 null)", example = "4.4")
        private Double grade;
    }
    public static TodayMenu from(List<Menu> menus){
        List<MenuInfo> menuInfoList = new ArrayList<>();
        for (Menu menu : menus) {
            MenuInfo menuInfo = new MenuInfo(menu.getId(), menu.getName(), menu.getPrice(), menu.getGrade());
            menuInfoList.add(menuInfo);
        }
        return new TodayMenu(menuInfoList);
    }
}
