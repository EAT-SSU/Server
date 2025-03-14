package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MealSection(Long mealId, String title, List<MenuLine> menuLines) implements SectionInBoard {

	public MealSection(Long mealId, String title) {
		this(mealId, title, new ArrayList<>());
	}

	public MealSection(Long mealId) {
		this(mealId, null, new ArrayList<>());
	}

	@Override
	public void addMenuLine(MenuLine menuLine) {
		menuLines.add(menuLine);
	}
}
