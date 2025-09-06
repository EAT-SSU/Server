package ssu.eatssu.domain.rating.entity;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.RatingsDto;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.repository.ReviewRepository;

import java.util.Collection;

/**
 * Jpa projection 을 이용하여 구현한 클래스입니다.
 * 더미 데이터
 * JpaLoadCollectionRatingCalculator : 253 lines
 * JpaProjectionRatingCalculator : 174 lines
 */

@RequiredArgsConstructor
public class JpaProjectionRatingCalculator implements RatingCalculator {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewRatingCount mealRatingCount(Meal meal) {
        ReviewRating.resetAll();

        Collection<RatingsDto> mealRatings = reviewRepository.findByMenu_MealMenus_Meal(meal, RatingsDto.class);
        mealRatings.forEach(ratings -> ReviewRating.fromValue(ratings.getMainRating()).incrementCount());

        return ReviewRating.toResponse();
    }

    @Override
    public ReviewRatingCount menuRatingCount(Menu menu) {
        return null;
    }

    @Override
    public RatingAverages mealAverageRatings(Meal meal) {
        long totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0)
            return new RatingAverages(null);

        Collection<RatingsDto> mealRatings = reviewRepository.findByMenu_MealMenus_Meal(meal, RatingsDto.class);

        Integer totalMainRating = mealRatings.stream().mapToInt(RatingsDto::getMainRating).sum();

        return RatingAverages.builder()
                             .mainRating(averageRating(totalMainRating, totalReviewCount))
                             .build();
    }

    @Override
    public RatingAverages menuAverageRatings(Menu menu) {
        return null;
    }

    @Override
    public Double mealAverageMainRating(Meal meal) {
        long totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0) {
            return null;
        }

        return averageRating(mealTotalMainRating(meal), totalReviewCount);
    }

    @Override
    public Double menuAverageMainRating(Menu menu) {
        return null;
    }

    @Override
    public long mealTotalReviewCount(Meal meal) {
        return reviewRepository.countByMenu_MealMenus_Meal(meal);
    }

    public Double averageRating(Integer totalRating, long totalReviewCount) {
        if (totalRating == null || totalReviewCount == 0) {
            return null;
        }
        return totalRating / (double) totalReviewCount;
    }

    public Integer mealTotalMainRating(Meal meal) {
        return null;
    }

}
