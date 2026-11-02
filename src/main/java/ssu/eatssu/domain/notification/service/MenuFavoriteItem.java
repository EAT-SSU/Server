package ssu.eatssu.domain.notification.service;

import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;

public record MenuFavoriteItem(String menuName, String menuNameEn, Restaurant restaurant, TimePart timePart) {

    public String localizedName(Language language) {
        if (language == null || language == Language.KO || menuNameEn == null || menuNameEn.isBlank()) {
            return menuName;
        }
        return menuNameEn;
    }
}
