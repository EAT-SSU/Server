package ssu.eatssu.domain.enums;

import lombok.Getter;

@Getter
public enum DayType {

    WEEKDAY("주중"),
    HOLIDAY("주말, 공휴일");

    private String krName;

    DayType(String krName) {
        this.krName = krName;
    }

}
