package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCalculator;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCounter;
import ssu.eatssu.domain.menu.persistence.QuerydslMenuRatingCalculator;
import ssu.eatssu.domain.menu.persistence.QuerydslMenuRatingCounter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReviewRatingServiceTest {

    @Test
    void 없는_별점은_0으로_집계한다() {
        QuerydslMealRatingCounter counter = mock(QuerydslMealRatingCounter.class);
        Meal meal = mock(Meal.class);
        when(meal.getId()).thenReturn(1L);
        when(counter.getRatingCountMap(1L)).thenReturn(Map.of(1, 2L, 5, 3L));
        ReviewRatingService service = new ReviewRatingService(mock(QuerydslMealRatingCalculator.class),
                                                               mock(QuerydslMenuRatingCalculator.class), counter,
                                                               mock(QuerydslMenuRatingCounter.class));

        var count = service.mealRatingCount(meal);

        assertThat(count.oneStarCount()).isEqualTo(2);
        assertThat(count.twoStarCount()).isZero();
        assertThat(count.fiveStarCount()).isEqualTo(3);
    }
}
