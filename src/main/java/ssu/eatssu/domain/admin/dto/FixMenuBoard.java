package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record FixMenuBoard(String restaurantName, List<MenuLine> menus) implements RestaurantMenuBoard {
    public FixMenuBoard(String restaurantName) {
        this(restaurantName, new ArrayList<>());
    }

    public void addMenu(MenuLine menu) {
        menus.add(menu);
    }
}