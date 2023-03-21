package ssu.eatssu.domain;

import lombok.Getter;

@Getter
public enum DayType {

    WEEKDAY("평일"),
    HOLIDAY("주말");

    private String krName;

    DayType(String krName) {
        this.krName = krName;
    }

}
