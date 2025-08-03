package ssu.eatssu.domain.rating.entity;

import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

public interface RatingCalculator {

    // 식단에 포함된 메뉴들의 평점 별 리뷰 개수 계산
    ReviewRatingCount mealRatingCount(Meal meal);

    // 각 메뉴의 평점 별 개수 계산
    ReviewRatingCount menuRatingCount(Menu menu);

    // 식단 평균 평점 세트 계산
    RatingAverages mealAverageRatings(Meal meal);

    // 메뉴 평균 평점 세트 계산
    RatingAverages menuAverageRatings(Menu menu);

    // 식단 메인 평점 평균 계산
    Double mealAverageMainRating(Meal meal);

    // 메뉴 메인 평점 평균 계산
    Double menuAverageMainRating(Menu menu);

    // 식단 총 리뷰 개수
    long mealTotalReviewCount(Meal meal);

    /**
     * 리뷰 평접 총합 계산용
     * nullable 의 합 계산을 위한 메소드입니다.
     */
    default Integer sum(Integer a, Integer b) {
        if (a == null)
            return b;
        if (b == null)
            return a;
        return a + b;
    }

}
