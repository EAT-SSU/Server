package ssu.eatssu.web.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ssu.eatssu.domain.Menu;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class TodayMenu {
    private List<MenuInfo> menuInfoList;

    @AllArgsConstructor
    @Getter
    private static class MenuInfo{
        private String name;
        private Integer price;
        private Double grade;
    }
    public static TodayMenu from(List<Menu> menus){
        List<MenuInfo> menuInfoList = new ArrayList<>();
        for (Menu menu : menus) {
            MenuInfo menuInfo = new MenuInfo(menu.getName(), menu.getPrice(), menu.getGrade());
            menuInfoList.add(menuInfo);
        }
        return new TodayMenu(menuInfoList);
    }
}
