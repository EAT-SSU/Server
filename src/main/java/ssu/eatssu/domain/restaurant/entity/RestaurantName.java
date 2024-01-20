package ssu.eatssu.domain.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Getter
public enum RestaurantName {
    DODAM("도담 식당", 6000),
    DORMITORY( "기숙사 식당", 5000),
    FOOD_COURT( "푸드 코트", null),
    SNACK_CORNER( "스낵 코너", null),
    HAKSIK( "학생 식당", 5000);

    private String description;
    private Integer price;

    @JsonCreator
    public static RestaurantName from(String description) {
        return Arrays.stream(RestaurantName.values())
            .filter(d -> d.getDescription().equals(description))
            .findAny()
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));
    }

    RestaurantName(String description, Integer price) {
        this.description = description;
        this.price = price;
    }

    public static boolean isFixed(RestaurantName restaurantName) {
        return restaurantName.getRestaurantType() == RestaurantType.FIXED;
    }

    public static boolean isVariable(RestaurantName restaurantName) {
        return restaurantName.getRestaurantType() == RestaurantType.VARIABLE;
    }
}
