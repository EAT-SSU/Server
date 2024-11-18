package ssu.eatssu.domain.menu.service;

import static org.assertj.core.api.Assertions.*;
import static ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.presentation.dto.request.CreateMealRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;

@SpringBootTest
@DisplayName("MealService 테스트")
@ActiveProfiles("test")
class MealServiceTest {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealRepository mealRepository;

    @BeforeEach
    void setUp() {
        mealRepository.deleteAll();
    }

    @Transactional
    @Test
    void 식단을_생성한다() {
        // when
        Long mealId = 식단_생성_요청();

        // then
        Optional<Meal> meal = mealRepository.findById(mealId);

        assertThat(meal).isPresent();
        assertThat(mealRepository.findAll()).hasSize(1);

        Meal createdMeal = meal.get();
        assertThat(createdMeal.getMenuNames()).containsExactlyInAnyOrder("돈까스", "샐러드", "김치");
        assertThat(createdMeal.getRestaurant().name()).isEqualTo("HAKSIK");
        assertThat(createdMeal.getTimePart()).isEqualTo(LUNCH);
    }

    private Long 식단_생성_요청() {
        // given & when
        Date date = Date.valueOf("2024-01-03");
        TimePart timePart = LUNCH;
        Restaurant restaurant = Restaurant.from("HAKSIK");
        CreateMealRequest request = new CreateMealRequest(List.of("돈까스", "샐러드", "김치"));

        // then
        return mealService.createMeal(date, restaurant, timePart, request);
    }

    @Test
    void 식단을_조회한다() {
        // given
        Long meadId = 식단_생성_요청();

        // when
        MenusInMealResponse response = mealService.getMenusInMealByMealId(meadId);

        // then
        assertThat(response.getBriefMenus()).hasSize(3);
    }

    @Test
    void 식단을_삭제한다() {
        // given
        Long mealId = 식단_생성_요청();

        // when
        mealService.deleteByMealId(mealId);

        // then
        assertThat(mealRepository.findAll()).hasSize(0);
        assertThat(mealRepository.findAll()).hasSize(0);
    }
}