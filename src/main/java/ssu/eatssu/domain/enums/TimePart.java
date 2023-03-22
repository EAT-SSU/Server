package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum TimePart {
    MORNING("조식"),
    LUNCH("중식"),
    DINNER("석식");

    @JsonCreator
    public static TimePart from(String s){
        return TimePart.valueOf(s.toUpperCase(Locale.ROOT));
    }

    private String krName;

    TimePart(String krName){
        this.krName = krName;
    }
}