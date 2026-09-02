package ssu.eatssu.domain.menu.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMainMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class MealServiceBranchTest {

    @Test
    void 고정_식당의_식단은_조회하지_않는다() {
        MealService service = new MealService(mock(MealRepository.class), mock(MealMenuRepository.class),
                                              mock(MealMainMenuRepository.class), mock(MealRatingService.class),
                                              mock(MenuService.class));

        assertThatThrownBy(() -> service.getMealDetailsByDateAndRestaurantAndTimePart(new Date(), Restaurant.FOOD_COURT,
                                                                                        TimePart.LUNCH, null))
                .isInstanceOf(BaseException.class);
    }
}
