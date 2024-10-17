package ssu.eatssu.domain.menu.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

    private final MealRepository mealRepository;
    private final MealRatingService mealRatingService;

    public List<MealDetailResponse> getMealDetailsByDateAndRestaurantAndTimePart(
        Date date,
        Restaurant restaurant,
        TimePart timePart) {

        validateMealRestaurant(restaurant);

        /**
         * 해당 날짜, 시간대, 식당에 해당하는 식단을 조회합니다. 식단은 여러 개 존재할 수 있습니다.
         */
        List<Meal> meals = findMealsByDateAndTimePartAndRestaurant(date, timePart, restaurant);

        return meals.stream()
            .map(meal -> MealDetailResponse.from(meal, mealRatingService.getMainRatingAverage(meal.getId())))
            .toList();
    }

    private List<Meal> findMealsByDateAndTimePartAndRestaurant(Date date, TimePart timePart, Restaurant restaurant) {
        return mealRepository.findAllByDateAndTimePartAndRestaurant(date, timePart, restaurant);
    }

    private void validateMealRestaurant(Restaurant restaurant) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }
}