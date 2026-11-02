package ssu.eatssu.domain.notification.persistence;

import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.service.MenuFavoriteItem;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

public record MenuFavoriteMatch(Long userId, String menuName, String menuNameEn, Restaurant restaurant,
                                TimePart timePart) {

    public MenuFavoriteItem toItem() {
        return new MenuFavoriteItem(menuName, menuNameEn, restaurant, timePart);
    }
}
