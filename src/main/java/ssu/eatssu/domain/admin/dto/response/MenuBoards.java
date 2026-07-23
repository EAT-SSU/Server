package ssu.eatssu.domain.admin.dto.response;

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