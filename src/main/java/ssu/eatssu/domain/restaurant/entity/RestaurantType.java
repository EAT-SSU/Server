package ssu.eatssu.domain.restaurant.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum RestaurantType {
    FIXED("고정메뉴 식당", Arrays.asList(Restaurant.FOOD_COURT, Restaurant.SNACK_CORNER)),
    VARIABLE("변동메뉴 식당", Arrays.asList(Restaurant.DODAM, Restaurant.DORMITORY, Restaurant.HAKSIK));

    private final String description;
    private final List<Restaurant> restaurants;

    RestaurantType(String description, List<Restaurant> restaurants) {
        this.description = description;
        this.restaurants = restaurants;
    }

    public static boolean isFixedType(Restaurant restaurant) {
        return FIXED.restaurants.contains(restaurant);
    }

    public static boolean isVariableType(Restaurant restaurant) {
        return VARIABLE.restaurants.contains(restaurant);
    }
}
