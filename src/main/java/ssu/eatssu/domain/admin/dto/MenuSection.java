package ssu.eatssu.domain.admin.dto;

import ssu.eatssu.domain.menu.entity.MenuCategory;

import java.util.ArrayList;
import java.util.List;

public record MenuSection(Long categoryId, String categoryName,  List<MenuLine> menuLines) implements SectionInBoard {
    public MenuSection(MenuCategory category) {
        this(category.getId(), category.getName(), new ArrayList<>());
    }

    @Override
    public void addMenuLine(MenuLine menuLine) {
        menuLines.add(menuLine);
    }
}