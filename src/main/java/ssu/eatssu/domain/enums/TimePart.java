package ssu.eatssu.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;
import lombok.Getter;

import java.util.Locale;

@Getter
public enum TimePart {
    MORNING("조식"),
    LUNCH("중식"),
    DINNER("석식");

    private String description;

    TimePart(String description) {
        this.description = description;
    }

    @JsonCreator
    public static TimePart from(String description) {
        return Arrays.stream(TimePart.values())
            .filter(d -> d.getDescription().equals(description))
            .findAny()
            .orElseThrow(IllegalArgumentException::new);
    }
}