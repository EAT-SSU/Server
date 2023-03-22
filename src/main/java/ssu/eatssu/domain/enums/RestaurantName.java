package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum RestaurantName {
    DODAM("도담 식당"),
    DOMITORY("기숙사 식당"),
    FOOD_COURT("푸드 코트"),
    SNACK_CORNER("스낵 코너"),
    HAKSIK("학생 식당"),
    THE_KITCHEN("더 키친");

    private String krName;

    @JsonCreator
    public static RestaurantName from(String s){
        return RestaurantName.valueOf(s.toUpperCase(Locale.ROOT));
    }

    RestaurantName(String krName) {
        this.krName = krName;
    }

}
