package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MenuBoard(String restaurantName, List<SectionInBoard> sections) {
    public MenuBoard(String restaurantName) {
        this(restaurantName, new ArrayList<>());
    }

    public void addSection(SectionInBoard section) {
        sections.add(section);
    }
}