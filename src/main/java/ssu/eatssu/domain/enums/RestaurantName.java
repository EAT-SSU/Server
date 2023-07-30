package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum RestaurantName {
    DODAM("도담 식당", 6000),
    DOMITORY("기숙사 식당", 5000),
    FOOD_COURT("푸드 코트", 0),
    SNACK_CORNER("스낵 코너", 0),
    HAKSIK("학생 식당", 5000),
    THE_KITCHEN("더 키친", 0);

    private String krName;
    private Integer price;

    @JsonCreator
    public static RestaurantName from(String s){
        return RestaurantName.valueOf(s.toUpperCase(Locale.ROOT));
    }

    RestaurantName(String krName, Integer price) {
        this.krName = krName;
        this.price = price;
    }

}
