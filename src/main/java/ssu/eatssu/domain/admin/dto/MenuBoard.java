package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MenuBoard(String restaurantName, List<MenuSection> menuSections) {
    public MenuBoard(String restaurantName) {
        this(restaurantName, new ArrayList<>());
    }

    public void addMenuSection(MenuSection menuSection) {
        menuSections.add(menuSection);
    }
}