package ssu.eatssu.domain.restaurant.entity;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public enum RestaurantType {
    FIXED("고정메뉴 식당", Arrays.asList(RestaurantName.FOOD_COURT, RestaurantName.SNACK_CORNER)),
    VARIABLE("변동메뉴 식당", Arrays.asList(RestaurantName.DODAM, RestaurantName.DORMITORY, RestaurantName.HAKSIK));

    private String description;
    private List<RestaurantName> restaurants;

    RestaurantType(String description, List<RestaurantName> restaurants) {
        this.description = description;
        this.restaurants = restaurants;
    }

    public static boolean isFixedType(RestaurantName restaurantName) {
        return FIXED.restaurants.contains(restaurantName);
    }

    public static boolean isVARIABLEType(RestaurantName restaurantName) {
        return VARIABLE.restaurants.contains(restaurantName);
    }
}
