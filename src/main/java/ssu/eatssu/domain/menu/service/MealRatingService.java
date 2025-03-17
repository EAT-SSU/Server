package ssu.eatssu.domain.menu.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCalculator;

@Service
@RequiredArgsConstructor
public class MealRatingService {

	private final QuerydslMealRatingCalculator mealRatingCalculator;

	public Double getMainRatingAverage(Long mealId) {
		return mealRatingCalculator.getMainRatingAverage(mealId);
	}

}
