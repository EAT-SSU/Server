package ssu.eatssu.domain.notification.service;

import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

public enum MenuFavoriteMessageTemplate {

    KO("🍽️ 찜한 메뉴가 나온대요!",
       "🍽️ 찜한 메뉴가 2개나 나온대요!",
       "🍽️ 찜한 메뉴가 여러 개 나온대요!",
       restaurants("학생식당", "도담식당", "기숙사식당", "교직원식당", "더 키친", "스낵 코너"),
       timeParts("아침", "점심", "저녁")) {
        @Override
        String todayLabel(String time) {
            return "오늘 " + time;
        }

        @Override
        String dateLabel(LocalDate date, String time) {
            return "%d월 %d일 %s".formatted(date.getMonthValue(), date.getDayOfMonth(), time);
        }

        @Override
        String one(String when, String restaurant, String menu) {
            return "%s, %s에 %s 등장 👀".formatted(when, restaurant, menu);
        }

        @Override
        String two(String when, String firstRestaurant, String firstMenu, String secondRestaurant,
                   String secondMenu) {
            return "%s, %s %s · %s %s 등장 👀".formatted(when, firstRestaurant, firstMenu, secondRestaurant,
                                                         secondMenu);
        }

        @Override
        String many(String when, String restaurant, String menu, int others) {
            return "%s, %s %s 외 %d개 등장 👀".formatted(when, restaurant, menu, others);
        }
    },

    EN("🍽️ A favorite menu is available!",
       "🍽️ Two favorite menus are available!",
       "🍽️ Several favorite menus are available!",
       restaurants("Student cafeteria", "Dodam cafeteria", "Dormitory cafeteria", "Faculty cafeteria",
                   "더 키친", "Snack Corner"),
       timeParts("Breakfast", "Lunch", "Dinner")) {
        @Override
        String todayLabel(String time) {
            return "Today’s " + time;
        }

        @Override
        String dateLabel(LocalDate date, String time) {
            return "%s on %s".formatted(time, date.format(DateTimeFormatter.ofPattern("MMM d", Locale.ENGLISH)));
        }

        @Override
        String one(String when, String restaurant, String menu) {
            return "%s: %s at %s 👀".formatted(when, menu, restaurant);
        }

        @Override
        String two(String when, String firstRestaurant, String firstMenu, String secondRestaurant,
                   String secondMenu) {
            return "%s: %s at %s · %s at %s 👀".formatted(when, firstMenu, firstRestaurant, secondMenu,
                                                         secondRestaurant);
        }

        @Override
        String many(String when, String restaurant, String menu, int others) {
            return "%s: %s at %s and %d more 👀".formatted(when, menu, restaurant, others);
        }
    },

    JA("🍽️ お気に入りメニューが登場！",
       "🍽️ お気に入りメニューが2品登場！",
       "🍽️ お気に入りメニューが複数登場！",
       restaurants("学生食堂", "ドダム食堂", "寮食堂", "教職員食堂", "더 키친", "スナックコーナー"),
       timeParts("朝食", "昼食", "夕食")) {
        @Override
        String todayLabel(String time) {
            return "今日の" + time;
        }

        @Override
        String dateLabel(LocalDate date, String time) {
            return "%d月%d日の%s".formatted(date.getMonthValue(), date.getDayOfMonth(), time);
        }

        @Override
        String one(String when, String restaurant, String menu) {
            return "%s、%sに%sが登場 👀".formatted(when, restaurant, menu);
        }

        @Override
        String two(String when, String firstRestaurant, String firstMenu, String secondRestaurant,
                   String secondMenu) {
            return "%s、%sの%s・%sの%sが登場 👀".formatted(when, firstRestaurant, firstMenu, secondRestaurant,
                                                       secondMenu);
        }

        @Override
        String many(String when, String restaurant, String menu, int others) {
            return "%s、%sの%sほか%d品が登場 👀".formatted(when, restaurant, menu, others);
        }
    },

    VI("🍽️ Có món ăn yêu thích hôm nay!",
       "🍽️ Có hai món ăn yêu thích hôm nay!",
       "🍽️ Có nhiều món ăn yêu thích hôm nay!",
       restaurants("Nhà ăn sinh viên", "Nhà ăn Dodam", "Nhà ăn ký túc xá", "Nhà ăn giảng viên", "더 키친",
                   "Quầy đồ ăn nhẹ"),
       timeParts("Bữa sáng", "Bữa trưa", "Bữa tối")) {
        @Override
        String todayLabel(String time) {
            return time + " hôm nay";
        }

        @Override
        String dateLabel(LocalDate date, String time) {
            return "%s ngày %d/%d".formatted(time, date.getDayOfMonth(), date.getMonthValue());
        }

        @Override
        String one(String when, String restaurant, String menu) {
            return "%s: %s tại %s 👀".formatted(when, menu, restaurant);
        }

        @Override
        String two(String when, String firstRestaurant, String firstMenu, String secondRestaurant,
                   String secondMenu) {
            return "%s: %s tại %s · %s tại %s 👀".formatted(when, firstMenu, firstRestaurant, secondMenu,
                                                           secondRestaurant);
        }

        @Override
        String many(String when, String restaurant, String menu, int others) {
            return "%s: %s tại %s và %d món khác 👀".formatted(when, menu, restaurant, others);
        }
    };

    private final String oneTitle;
    private final String twoTitle;
    private final String manyTitle;
    private final Map<Restaurant, String> restaurantNames;
    private final Map<TimePart, String> timePartNames;

    MenuFavoriteMessageTemplate(String oneTitle, String twoTitle, String manyTitle,
                                Map<Restaurant, String> restaurantNames, Map<TimePart, String> timePartNames) {
        this.oneTitle = oneTitle;
        this.twoTitle = twoTitle;
        this.manyTitle = manyTitle;
        this.restaurantNames = restaurantNames;
        this.timePartNames = timePartNames;
    }

    public static MenuFavoriteMessageTemplate of(Language language) {
        if (language == null) {
            return KO;
        }
        return valueOf(language.name());
    }

    abstract String todayLabel(String time);

    abstract String dateLabel(LocalDate date, String time);

    abstract String one(String when, String restaurant, String menu);

    abstract String two(String when, String firstRestaurant, String firstMenu, String secondRestaurant,
                        String secondMenu);

    abstract String many(String when, String restaurant, String menu, int others);

    String title(int menuCount) {
        if (menuCount == 1) {
            return oneTitle;
        }
        if (menuCount == 2) {
            return twoTitle;
        }
        return manyTitle;
    }

    String restaurantName(Restaurant restaurant) {
        return restaurantNames.get(restaurant);
    }

    String timePartName(TimePart timePart) {
        return timePartNames.get(timePart);
    }

    private static Map<Restaurant, String> restaurants(String haksik, String dodam, String dormitory, String faculty,
                                                       String foodCourt, String snackCorner) {
        return new EnumMap<>(Map.of(
                Restaurant.HAKSIK, haksik,
                Restaurant.DODAM, dodam,
                Restaurant.DORMITORY, dormitory,
                Restaurant.FACULTY, faculty,
                Restaurant.FOOD_COURT, foodCourt,
                Restaurant.SNACK_CORNER, snackCorner));
    }

    private static Map<TimePart, String> timeParts(String morning, String lunch, String dinner) {
        return new EnumMap<>(Map.of(
                TimePart.MORNING, morning,
                TimePart.LUNCH, lunch,
                TimePart.DINNER, dinner));
    }
}
