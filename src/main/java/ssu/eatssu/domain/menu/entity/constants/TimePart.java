package ssu.eatssu.domain.menu.entity.constants;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;

@Getter
public enum TimePart {
	MORNING("조식"),
	LUNCH("중식"),
	DINNER("석식");

	private final String description;

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