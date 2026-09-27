package ssu.eatssu.domain.notification.persistence;

import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

public record MenuFavoriteMatch(Long userId, String menuName, Restaurant restaurant, TimePart timePart) {

}
