package ssu.eatssu.domain.menu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.domain.menu.dto.MenuRequest.CreateMealRequest;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@RequiredArgsConstructor
public class MenuValidator {

    public static boolean validateExistedMeal(List<Meal> meals, CreateMealRequest request) {

        List<String> sortedRequestMenuNames = new ArrayList<>(request.getMenuNames());
        Collections.sort(sortedRequestMenuNames);

        return meals.stream().anyMatch(meal -> {
            List<String> sortedMenuNames = new ArrayList<>(meal.getMenuNames());
            Collections.sort(sortedMenuNames);
            return sortedMenuNames.equals(sortedRequestMenuNames);
        });
    }
}
