package ssu.eatssu.domain.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import ssu.eatssu.domain.restaurant.RestaurantName;

import static ssu.eatssu.domain.restaurant.RestaurantName.*;


@Getter
public enum MenuTypeGroup {
    FIX("고정 메뉴", Arrays.asList(FOOD_COURT, SNACK_CORNER)),
    CHANGE("변동 메뉴", Arrays.asList(DODAM, DORMITORY, HAKSIK));



    private String krName;
    private List<RestaurantName> restaurantList;

    MenuTypeGroup(String krName, List<RestaurantName> restaurantList){
        this.krName = krName;
        this.restaurantList = restaurantList;
    }

    public static boolean isFix(RestaurantName restaurant){
        return FIX.getRestaurantList().contains(restaurant);
    }
    public static boolean isChange(RestaurantName restaurant){
        return CHANGE.getRestaurantList().contains(restaurant);
    }

    @JsonCreator
    public static MenuTypeGroup from(String s){
        return MenuTypeGroup.valueOf(s.toUpperCase(Locale.ROOT));
    }
}
