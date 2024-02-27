package ssu.eatssu.domain.menu.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.dto.MealCreateRequest;
import ssu.eatssu.domain.menu.entity.Meal;

@RequiredArgsConstructor
public class MenuValidator {

    public static boolean validateExistedMeal(List<Meal> meals, MealCreateRequest request) {

        List<String> sortedRequestMenuNames = new ArrayList<>(request.menuNames());
        Collections.sort(sortedRequestMenuNames);

        return meals.stream().anyMatch(meal -> {
            List<String> sortedMenuNames = new ArrayList<>(meal.getMenuNames());
            Collections.sort(sortedMenuNames);
            return sortedMenuNames.equals(sortedRequestMenuNames);
        });
    }
}
