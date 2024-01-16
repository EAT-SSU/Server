package ssu.eatssu.domain.menu.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;

import static ssu.eatssu.domain.restaurant.entity.RestaurantName.*;


@Getter
public enum MenuType {
    FIXED("고정 메뉴", Arrays.asList(FOOD_COURT, SNACK_CORNER)),
    CHANGED("변동 메뉴", Arrays.asList(DODAM, DORMITORY, HAKSIK));

    private final String description;
    private final List<RestaurantName> restaurants;

    MenuType(String description, List<RestaurantName> restaurants) {
        this.description = description;
        this.restaurants = restaurants;
    }

    public static boolean isFixed(RestaurantName restaurant) {
        return FIXED.getRestaurants().contains(restaurant);
    }

    public static boolean isChanged(RestaurantName restaurant) {
        return CHANGED.getRestaurants().contains(restaurant);
    }

    @JsonCreator
    public static MenuType from(String description) {
        return Arrays.stream(MenuType.values())
            .filter(d -> d.getDescription().equals(description))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }
}
