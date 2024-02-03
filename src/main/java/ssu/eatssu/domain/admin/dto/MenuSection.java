package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MenuSection(String category, List<MenuLine> menuLines) implements SectionInBoard{
    public MenuSection(String category) {
        this(category, new ArrayList<>());
    }
    @Override
    public void addMenuLine(MenuLine menuLine) {
        menuLines.add(menuLine);
    }
}