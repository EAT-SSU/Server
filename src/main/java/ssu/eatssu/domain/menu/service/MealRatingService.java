package ssu.eatssu.domain.menu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.repository.QuerydslMealRatingCalculator;

@Service
@RequiredArgsConstructor
public class MealRatingService {

    private final QuerydslMealRatingCalculator mealRatingCalculator;

    public Double getMainRatingAverage(Long mealId) {
        return mealRatingCalculator.getMainRatingAverage(mealId);
    }


}
