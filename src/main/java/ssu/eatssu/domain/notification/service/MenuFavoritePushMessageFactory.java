package ssu.eatssu.domain.notification.service;

import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.infrastructure.PushMessage;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatch;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MenuFavoritePushMessageFactory {

    static final String PUSH_TYPE = "MENU_FAVORITE";

    private static final Map<Restaurant, String> RESTAURANT_NAMES = new EnumMap<>(Map.of(
            Restaurant.HAKSIK, "학생식당",
            Restaurant.DODAM, "도담식당",
            Restaurant.DORMITORY, "기숙사식당",
            Restaurant.FACULTY, "교직원식당",
            Restaurant.FOOD_COURT, "푸드코트",
            Restaurant.SNACK_CORNER, "스낵코너"));

    private static final Map<TimePart, String> TIME_PART_NAMES = new EnumMap<>(Map.of(
            TimePart.MORNING, "아침",
            TimePart.LUNCH, "점심",
            TimePart.DINNER, "저녁"));

    public PushMessage create(LocalDate date, List<MenuFavoriteMatch> matches) {
        List<MenuFavoriteMatch> sortedMatches = matches.stream()
                                                       .sorted(Comparator.comparing(MenuFavoriteMatch::timePart)
                                                                         .thenComparing(MenuFavoriteMatch::restaurant)
                                                                         .thenComparing(MenuFavoriteMatch::menuName))
                                                       .toList();
        Map<String, List<Restaurant>> restaurantsByMenu = groupRestaurantsByMenu(sortedMatches);
        List<String> menuNames = new ArrayList<>(restaurantsByMenu.keySet());
        String timeLabel = timeLabel(sortedMatches);

        String firstMenu = menuNames.get(0);
        List<Restaurant> firstMenuRestaurants = restaurantsByMenu.get(firstMenu);
        String firstRestaurant = RESTAURANT_NAMES.get(firstMenuRestaurants.get(0));

        Map<String, String> data = Map.of(
                "type", PUSH_TYPE,
                "date", date.format(DateTimeFormatter.BASIC_ISO_DATE),
                "timePart", sortedMatches.get(0).timePart().name());

        if (menuNames.size() == 1 && firstMenuRestaurants.size() == 1) {
            return new PushMessage("🍽️ 찜한 메뉴가 나온대요!",
                                   "%s, %s에 %s 등장 👀".formatted(timeLabel, firstRestaurant, firstMenu), data);
        }
        if (menuNames.size() == 1) {
            return new PushMessage("🍽️ 찜한 메뉴가 여러 곳에 나온대요!",
                                   "%s, %s 외 %d곳에 %s 등장 👀".formatted(timeLabel, firstRestaurant,
                                                                       firstMenuRestaurants.size() - 1, firstMenu),
                                   data);
        }
        if (menuNames.size() == 2) {
            String secondMenu = menuNames.get(1);
            String secondRestaurant = RESTAURANT_NAMES.get(restaurantsByMenu.get(secondMenu).get(0));
            return new PushMessage("🍽️ 찜한 메뉴가 2개나 나온대요!",
                                   "%s, %s %s · %s %s 등장 👀".formatted(timeLabel, firstRestaurant, firstMenu,
                                                                      secondRestaurant, secondMenu),
                                   data);
        }
        return new PushMessage("🍽️ 찜한 메뉴가 여러 개 나온대요!",
                               "%s, %s %s 외 %d개 등장 👀".formatted(timeLabel, firstRestaurant, firstMenu,
                                                                   menuNames.size() - 1),
                               data);
    }

    private Map<String, List<Restaurant>> groupRestaurantsByMenu(List<MenuFavoriteMatch> sortedMatches) {
        Map<String, List<Restaurant>> restaurantsByMenu = new LinkedHashMap<>();
        for (MenuFavoriteMatch match : sortedMatches) {
            List<Restaurant> restaurants = restaurantsByMenu.computeIfAbsent(match.menuName(), key -> new ArrayList<>());
            if (!restaurants.contains(match.restaurant())) {
                restaurants.add(match.restaurant());
            }
        }
        return restaurantsByMenu;
    }

    private String timeLabel(List<MenuFavoriteMatch> sortedMatches) {
        return sortedMatches.stream()
                            .map(MenuFavoriteMatch::timePart)
                            .distinct()
                            .map(TIME_PART_NAMES::get)
                            .collect(Collectors.joining("·", "오늘 ", ""));
    }
}
