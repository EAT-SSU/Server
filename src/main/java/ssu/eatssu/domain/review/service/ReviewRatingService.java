package ssu.eatssu.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCalculator;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCounter;
import ssu.eatssu.domain.menu.persistence.QuerydslMenuRatingCalculator;
import ssu.eatssu.domain.menu.persistence.QuerydslMenuRatingCounter;
import ssu.eatssu.domain.rating.entity.RatingCalculator;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReviewRatingService implements RatingCalculator {

    private final QuerydslMealRatingCalculator mealRatingCalculator;
    private final QuerydslMenuRatingCalculator menuRatingCalculator;
    private final QuerydslMealRatingCounter mealRatingCounter;
    private final QuerydslMenuRatingCounter menuRatingCounter;

    @Override
    public ReviewRatingCount mealRatingCount(Meal meal) {
        Map<Integer, Long> ratingCountMap = mealRatingCounter.getRatingCountMap(meal.getId());
        return getRatingCount(ratingCountMap);
    }

    @Override
    public ReviewRatingCount menuRatingCount(Menu menu) {
        Map<Integer, Long> ratingCountMap = menuRatingCounter.getRatingCountMap(menu.getId());
        return getRatingCount(ratingCountMap);
    }

    @Override
    public RatingAverages mealAverageRatings(Meal meal) {
        Double mainRatingAverage = mealRatingCalculator.getMainRatingAverage(meal.getId());
        Double amountRatingAverage = mealRatingCalculator.getAmountRatingAverage(meal.getId());
        Double tasteRatingAverage = mealRatingCalculator.getTasteRatingAverage(meal.getId());
        return new RatingAverages(mainRatingAverage, amountRatingAverage, tasteRatingAverage);
    }

    @Override
    public RatingAverages menuAverageRatings(Menu menu) {
        Double mainRatingAverage = menuRatingCalculator.getMainRatingAverage(menu.getId());
        Double amountRatingAverage = menuRatingCalculator.getAmountRatingAverage(menu.getId());
        Double tasteRatingAverage = menuRatingCalculator.getTasteRatingAverage(menu.getId());
        return new RatingAverages(mainRatingAverage, amountRatingAverage, tasteRatingAverage);
    }

    @Override
    public Double mealAverageMainRating(Meal meal) {
        return mealRatingCalculator.getMainRatingAverage(meal.getId());
    }

    @Override
    public Double menuAverageMainRating(Menu menu) {
        return menuRatingCalculator.getMainRatingAverage(menu.getId());
    }

    @Override
    public long mealTotalReviewCount(Meal meal) {
        return mealRatingCounter.getTotalRatingCount(meal.getId());
    }

    private ReviewRatingCount getRatingCount(Map<Integer, Long> ratingCountMap) {
        long oneStarCount = getCount(ratingCountMap, 1);
        long twoStarCount = getCount(ratingCountMap, 2);
        long threeStarCount = getCount(ratingCountMap, 3);
        long fourStarCount = getCount(ratingCountMap, 4);
        long fiveStarCount = getCount(ratingCountMap, 5);
        return new ReviewRatingCount(oneStarCount, twoStarCount, threeStarCount, fourStarCount, fiveStarCount);
    }

    private long getCount(Map<Integer, Long> ratingCountMap, int star) {
        return ratingCountMap.getOrDefault(star, 0L);
    }
}
