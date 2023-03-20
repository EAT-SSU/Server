package ssu.eatssu.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum RestaurantName {
    DODAM("도담 식당"),
    DOMITORY("기숙사 식당"),
    FOOD_COURT("푸드 코트"),
    SNACK_CORNER("스낵 코너"),
    HAKSIK("학생 식당");

    private String krName;

    @JsonCreator
    public static RestaurantName from(String s){
        return RestaurantName.valueOf(s.toUpperCase(Locale.ROOT));
    }

    RestaurantName(String krName) {
        this.krName = krName;
    }

    public String getKrName() {
        return this.krName;
    }
}
