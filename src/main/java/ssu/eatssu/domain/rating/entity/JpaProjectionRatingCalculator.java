package ssu.eatssu.domain.rating.entity;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.dto.RatingsDto;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

import java.util.Collection;

/**
 * Jpa projection 을 이용하여 구현한 클래스입니다.
 * 더미 데이터
 * JpaLoadCollectionRatingCalculator : 253 lines
 * JpaProjectionRatingCalculator : 174 lines
 */

@Component
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
        int totalReviewCount = mealTotalReviewCount(meal);

        if (totalReviewCount == 0)
            return new RatingAverages(null, null, null);

        Collection<RatingsDto> mealRatings = reviewRepository.findByMenu_MealMenus_Meal(meal, RatingsDto.class);

        Integer totalMainRating = mealRatings.stream().mapToInt(RatingsDto::getMainRating).sum();
        Integer totalTasteRating = mealRatings.stream().mapToInt(RatingsDto::getTasteRating).sum();
        Integer totalAmountRating = mealRatings.stream().mapToInt(RatingsDto::getAmountRating).sum();

        return RatingAverages.builder()
                .mainRating(averageRating(totalMainRating, totalReviewCount))
                .tasteRating(averageRating(totalTasteRating, totalReviewCount))
                .amountRating(averageRating(totalAmountRating, totalReviewCount))
                .build();
    }

    @Override
    public RatingAverages menuAverageRatings(Menu menu) {
        return null;
    }

    @Override
    public Double mealAverageMainRating(Meal meal) {
        int totalReviewCount = mealTotalReviewCount(meal);

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
    public int mealTotalReviewCount(Meal meal) {
        return reviewRepository.countByMenu_MealMenus_Meal(meal).intValue();
    }

    @Override
    public Double averageRating(Integer totalRating, int totalReviewCount) {
        if (totalRating == null || totalReviewCount == 0) {
            return null;
        }
        return totalRating / (double) totalReviewCount;
    }

    public Integer mealTotalMainRating(Meal meal) {
        return null;
    }

}
