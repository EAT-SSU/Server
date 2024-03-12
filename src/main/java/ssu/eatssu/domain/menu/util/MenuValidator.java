package ssu.eatssu.domain.menu.util;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.entity.Meal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class MenuValidator {

    public static boolean validateExistedMeal(List<Meal> meals, List<String> menuNames) {

        List<String> sortedRequestMenuNames = new ArrayList<>(menuNames);
        Collections.sort(sortedRequestMenuNames);

        return meals.stream().anyMatch(meal -> {
            List<String> sortedMenuNames = new ArrayList<>(meal.getMenuNames());
            Collections.sort(sortedMenuNames);
            return sortedMenuNames.equals(sortedRequestMenuNames);
        });
    }
}
