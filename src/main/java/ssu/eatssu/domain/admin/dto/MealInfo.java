package ssu.eatssu.domain.admin.dto;

import java.util.Date;

import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

public record MealInfo(Restaurant restaurant, Date date, TimePart timePart) {
}
