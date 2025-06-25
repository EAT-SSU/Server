package ssu.eatssu.domain.admin.dto;

import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

import java.util.Date;

public record MealInfo(Restaurant restaurant, Date date, TimePart timePart) {
}
