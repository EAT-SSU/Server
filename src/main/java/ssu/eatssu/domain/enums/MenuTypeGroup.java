package ssu.eatssu.domain.enums;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ssu.eatssu.domain.enums.RestaurantName.*;


@Getter
public enum MenuTypeGroup {
    FIX("고정 메뉴", Arrays.asList(FOOD_COURT, SNACK_CORNER, THE_KITCHEN)),
    CHANGE("변동 메뉴", Arrays.asList(DODAM, DOMITORY, HAKSIK));

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
}
