package ssu.eatssu.domain.restaurant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum RestaurantName {
    DODAM("도담 식당", 6000),
    DORMITORY("기숙사 식당", 5000),
    FOOD_COURT("푸드 코트", 0),
    SNACK_CORNER("스낵 코너", 0),
    HAKSIK("학생 식당", 5000);

    private String description;
    private Integer price;

    @JsonCreator
    public static RestaurantName from(String description) {
        return RestaurantName.valueOf(description.toUpperCase(Locale.ROOT));
    }

    RestaurantName(String description, Integer price) {
        this.description = description;
        this.price = price;
    }

}