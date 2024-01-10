package ssu.eatssu.domain.enums;

import lombok.Getter;

@Getter
public enum DayType {

    WEEKDAY("주중"),
    HOLIDAY("주말, 공휴일");

    private final String description;

    DayType(String description) {
        this.description = description;
    }

}
