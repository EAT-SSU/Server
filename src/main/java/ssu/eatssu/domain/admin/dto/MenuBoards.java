package ssu.eatssu.domain.admin.dto;

import java.util.ArrayList;
import java.util.List;

public record MenuBoards(List<RestaurantMenuBoard> menuBoards) {
    public MenuBoards() {
        this(new ArrayList<>());
    }
    public void add(RestaurantMenuBoard menuBoard) {
        menuBoards.add(menuBoard);
    }
}