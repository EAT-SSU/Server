package ssu.eatssu.domain.notification.service;

import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.infrastructure.PushMessage;
import ssu.eatssu.domain.user.entity.Language;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MenuFavoritePushMessageFactory {

    static final String PUSH_TYPE = "MENU_FAVORITE";

    private static final Comparator<MenuFavoriteItem> ITEM_ORDER =
            Comparator.comparing(MenuFavoriteItem::timePart)
                      .thenComparing(MenuFavoriteItem::restaurant)
                      .thenComparing(MenuFavoriteItem::menuName);

    public PushMessage createPush(LocalDate date, List<MenuFavoriteItem> items, Language language,
                                  Long notificationId) {
        NotificationText text = createText(date, date, items, language);
        Map<String, String> data = Map.of(
                "type", PUSH_TYPE,
                "date", date.format(DateTimeFormatter.BASIC_ISO_DATE),
                "timePart", primaryTimePart(items).name(),
                "notificationId", String.valueOf(notificationId));
        return new PushMessage(text.title(), text.body(), data);
    }

    public NotificationText createText(LocalDate targetDate, LocalDate today, List<MenuFavoriteItem> items,
                                       Language language) {
        MenuFavoriteMessageTemplate template = MenuFavoriteMessageTemplate.of(language);
        List<MenuFavoriteItem> sortedItems = sort(items);
        List<MenuFavoriteItem> menus = distinctMenus(sortedItems);

        String time = sortedItems.stream()
                                 .map(MenuFavoriteItem::timePart)
                                 .distinct()
                                 .map(template::timePartName)
                                 .collect(Collectors.joining("·"));
        String when = targetDate.equals(today) ? template.todayLabel(time) : template.dateLabel(targetDate, time);

        MenuFavoriteItem first = menus.get(0);
        String firstRestaurant = template.restaurantName(first.restaurant());
        String firstMenu = first.localizedName(language);

        String body;
        if (menus.size() == 1) {
            body = template.one(when, firstRestaurant, firstMenu);
        } else if (menus.size() == 2) {
            MenuFavoriteItem second = menus.get(1);
            body = template.two(when, firstRestaurant, firstMenu, template.restaurantName(second.restaurant()),
                                second.localizedName(language));
        } else {
            body = template.many(when, firstRestaurant, firstMenu, menus.size() - 1);
        }
        return new NotificationText(template.title(menus.size()), body);
    }

    public TimePart primaryTimePart(List<MenuFavoriteItem> items) {
        return sort(items).get(0).timePart();
    }

    private List<MenuFavoriteItem> sort(List<MenuFavoriteItem> items) {
        return items.stream().sorted(ITEM_ORDER).toList();
    }

    private List<MenuFavoriteItem> distinctMenus(List<MenuFavoriteItem> sortedItems) {
        Map<String, MenuFavoriteItem> menus = new LinkedHashMap<>();
        sortedItems.forEach(item -> menus.putIfAbsent(item.restaurant() + "|" + item.menuName(), item));
        return List.copyOf(menus.values());
    }
}
