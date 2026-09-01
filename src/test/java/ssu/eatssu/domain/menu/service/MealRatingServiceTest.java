package ssu.eatssu.domain.menu.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.persistence.QuerydslMealRatingCalculator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class MealRatingServiceTest {

    @Test
    void getMainRatingAverageDelegatesToCalculator() {
        QuerydslMealRatingCalculator calculator = mock(QuerydslMealRatingCalculator.class);
        given(calculator.getMainRatingAverage(1L)).willReturn(4.5);

        Double average = new MealRatingService(calculator).getMainRatingAverage(1L);

        assertThat(average).isEqualTo(4.5);
    }
}
