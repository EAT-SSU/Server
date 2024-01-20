package ssu.eatssu.domain.restaurant.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Arrays;

@Getter
public enum RestaurantName {
    DODAM("도담 식당", 6000),
    DORMITORY("기숙사 식당", 5000),
    FOOD_COURT("푸드 코트", null),
    SNACK_CORNER("스낵 코너", null),
    HAKSIK("학생 식당", 5000);

    private String description;
    private Integer price;

    RestaurantName(String description, Integer price) {
        this.description = description;
        this.price = price;
    }

    @JsonCreator
    public static RestaurantName from(String description) {
        return Arrays.stream(RestaurantName.values())
                .filter(d -> d.getDescription().equals(description))
                .findAny()
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));
    }
}
