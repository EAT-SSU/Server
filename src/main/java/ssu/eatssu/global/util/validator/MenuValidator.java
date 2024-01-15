package ssu.eatssu.global.util.validator;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.enums.TimePart;
import ssu.eatssu.domain.menu.dto.MenuRequest.CreateMealRequest;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.restaurant.Restaurant;

@RequiredArgsConstructor
public class MenuValidator {

    public static boolean validateExistedMeal(List<Meal> meals, TimePart timePart, Date date,
        Restaurant restaurant, CreateMealRequest request) {
        Collections.sort(request.getMenuNames());

        meals.stream().forEach(
            meal -> {
	List<String> menuNames = meal.getMenuNames();
	Collections.sort(menuNames);

	if (menuNames.equals(request.getMenuNames())) {
	    return;
	}
            }
        );
        return false;
    }
}
