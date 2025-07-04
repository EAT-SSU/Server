package ssu.eatssu.domain.menu.entity.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Arrays;
import java.util.List;

import static ssu.eatssu.domain.restaurant.entity.Restaurant.DODAM;
import static ssu.eatssu.domain.restaurant.entity.Restaurant.DORMITORY;
import static ssu.eatssu.domain.restaurant.entity.Restaurant.FOOD_COURT;
import static ssu.eatssu.domain.restaurant.entity.Restaurant.HAKSIK;
import static ssu.eatssu.domain.restaurant.entity.Restaurant.SNACK_CORNER;

@Getter
public enum MenuType {
    FIXED("고정 메뉴", Arrays.asList(FOOD_COURT, SNACK_CORNER)),
    VARIABLE("변동 메뉴", Arrays.asList(DODAM, DORMITORY, HAKSIK));

    private final String description;
    private final List<Restaurant> restaurants;

    MenuType(String description, List<Restaurant> restaurants) {
        this.description = description;
        this.restaurants = restaurants;
    }

    @JsonCreator
    public static MenuType from(String description) {
        return Arrays.stream(MenuType.values())
                     .filter(d -> d.getDescription().equals(description))
                     .findAny()
                     .orElseThrow(IllegalArgumentException::new);
    }
}
