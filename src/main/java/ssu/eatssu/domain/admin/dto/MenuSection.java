package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MenuSection(String title, List<MenuLine> menuLines) {
    public MenuSection (String title) {
        this(title, new ArrayList<>());
    }

    public MenuSection (){
        this(null, new ArrayList<>());
    }

    public void addMenuLine(MenuLine menuLine) {
        menuLines.add(menuLine);
    }
}