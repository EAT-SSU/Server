package ssu.eatssu.domain.menu.presentation.dto.response;

import java.util.List;

public record MealSlotReconcileResult(
        List<Long> mealIds,
        List<List<String>> unmatchedMainMenus,
        List<Long> deletedMealIds,
        List<Long> keptWithReviews
) {
}
