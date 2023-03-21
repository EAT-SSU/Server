package ssu.eatssu.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Locale;

public enum TimePart {
    MORNING, LUNCH, DINNER;

    @JsonCreator
    public static TimePart from(String s){
        return TimePart.valueOf(s.toUpperCase(Locale.ROOT));
    }
}