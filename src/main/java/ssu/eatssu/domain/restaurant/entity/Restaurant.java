package ssu.eatssu.domain.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;


@Getter
public enum Restaurant {
    DODAM(RestaurantType.VARIABLE, "DODAM", 6000),
    DORMITORY(RestaurantType.VARIABLE, "DORMITORY", 5000),
    FOOD_COURT(RestaurantType.FIXED, "FOOD_COURT", null),
    SNACK_CORNER(RestaurantType.FIXED, "SNACK_CORNER", null),
    HAKSIK(RestaurantType.VARIABLE, "HAKSIK", 5000);

    private RestaurantType restaurantType;
    private String restaurantName;
    private Integer restaurantPrice;

    public static Restaurant from(String restaurantName) {
        return Arrays.stream(Restaurant.values())
            .filter(r -> r.getRestaurantName().equals(restaurantName))
            .findAny()
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));
    }

    Restaurant(RestaurantType restaurantType, String description, Integer price) {
        this.restaurantType = restaurantType;
        this.restaurantName = description;
        this.restaurantPrice = price;
    }

    public boolean isFixed() {
        return this.restaurantType == RestaurantType.FIXED;
    }

    public boolean isVariable() {
        return this.restaurantType == RestaurantType.VARIABLE;
    }
}