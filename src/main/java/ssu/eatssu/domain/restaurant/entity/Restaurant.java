package ssu.eatssu.domain.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;


@Getter
public enum Restaurant {
    DODAM( "도담 식당", 6000),
    DORMITORY( "기숙사 식당", 5000),
    FOOD_COURT("푸드 코트", null),
    SNACK_CORNER( "스낵 코너", null),
    HAKSIK( "학생 식당", 5000);

    private String restaurantName;
    private Integer restaurantPrice;

    public static Restaurant from(String restaurantName) {
        return Arrays.stream(Restaurant.values())
            .filter(r -> r.getRestaurantName().equals(restaurantName))
            .findAny()
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));
    }

    Restaurant( String description, Integer price) {
        this.restaurantName = description;
        this.restaurantPrice = price;
    }
}