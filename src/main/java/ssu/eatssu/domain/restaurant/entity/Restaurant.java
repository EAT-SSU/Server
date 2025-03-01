package ssu.eatssu.domain.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Arrays;


@Getter
public enum Restaurant {

    DODAM("DODAM", 6000),
    DORMITORY("DORMITORY", 5500),
    FOOD_COURT("FOOD_COURT", null),
    SNACK_CORNER("SNACK_CORNER", null),
    HAKSIK("HAKSIK", 5000);

    private String restaurantName;
    private Integer restaurantPrice;

    Restaurant(String restaurantName, Integer price) {
        this.restaurantName = restaurantName;
        this.restaurantPrice = price;
    }

    @JsonCreator
    public static Restaurant from(String restaurantName) {
        return Arrays.stream(Restaurant.values())
                .filter(r -> r.getRestaurantName().equals(restaurantName))
                .findAny()
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));
    }
}