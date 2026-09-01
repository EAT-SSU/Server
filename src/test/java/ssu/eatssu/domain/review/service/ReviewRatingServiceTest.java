package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCalculator;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCounter;
import ssu.eatssu.domain.menu.persistence.QuerydslMenuRatingCalculator;
import ssu.eatssu.domain.menu.persistence.QuerydslMenuRatingCounter;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReviewRatingServiceTest {

    private final QuerydslMealRatingCalculator mealRatingCalculator = mock(QuerydslMealRatingCalculator.class);
    private final QuerydslMenuRatingCalculator menuRatingCalculator = mock(QuerydslMenuRatingCalculator.class);
    private final QuerydslMealRatingCounter mealRatingCounter = mock(QuerydslMealRatingCounter.class);
    private final QuerydslMenuRatingCounter menuRatingCounter = mock(QuerydslMenuRatingCounter.class);
    private final ReviewRatingService service = new ReviewRatingService(mealRatingCalculator, menuRatingCalculator,
            mealRatingCounter, menuRatingCounter);

    @Test
    void 없는_별점은_0으로_집계한다() {
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        when(mealRatingCounter.getRatingCountMap(1L)).thenReturn(Map.of(1, 2L, 5, 3L));

        ReviewRatingCount count = service.mealRatingCount(meal);

        assertThat(count.oneStarCount()).isEqualTo(2);
        assertThat(count.twoStarCount()).isZero();
        assertThat(count.fiveStarCount()).isEqualTo(3);
    }

    @Test
    void 메뉴_별점_개수를_집계한다() {
        Menu menu = mock(Menu.class);
        when(menu.getId()).thenReturn(1L);
        when(menuRatingCounter.getRatingCountMap(1L)).thenReturn(Map.of(4, 5L));

        ReviewRatingCount count = service.menuRatingCount(menu);

        assertThat(count.fourStarCount()).isEqualTo(5);
    }

    @Test
    void 식단_평균_평점을_계산한다() {
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        when(mealRatingCalculator.getMainRatingAverage(1L)).thenReturn(4.5);

        RatingAverages averages = service.mealAverageRatings(meal);

        assertThat(averages.mainRating()).isEqualTo(4.5);
        assertThat(service.mealAverageMainRating(meal)).isEqualTo(4.5);
    }

    @Test
    void 메뉴_평균_평점을_계산한다() {
        Menu menu = mock(Menu.class);
        when(menu.getId()).thenReturn(1L);
        when(menuRatingCalculator.getMainRatingAverage(1L)).thenReturn(3.5);

        RatingAverages averages = service.menuAverageRatings(menu);

        assertThat(averages.mainRating()).isEqualTo(3.5);
        assertThat(service.menuAverageMainRating(menu)).isEqualTo(3.5);
    }

    @Test
    void 식단_총_리뷰_개수를_반환한다() {
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        when(mealRatingCounter.getTotalRatingCount(1L)).thenReturn(7L);

        assertThat(service.mealTotalReviewCount(meal)).isEqualTo(7L);
    }
}
