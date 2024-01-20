package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MenuBoards(List<MenuBoard> menuBoards) {
    public MenuBoards() {
        this(new ArrayList<>());
    }

    public void add(MenuBoard menuBoard) {
        menuBoards.add(menuBoard);
    }
}