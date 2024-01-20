package ssu.eatssu.domain.admin.dto;

import java.util.List;

public record FixMenuBoard(String restaurantName, List<BriefMenu> menus ) implements RestaurantMenuBoard {
    public void addMenu(BriefMenu menu) {
        menus.add(menu);
    }
}