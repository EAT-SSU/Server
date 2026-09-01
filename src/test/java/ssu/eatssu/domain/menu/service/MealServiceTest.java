package ssu.eatssu.domain.menu.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMainMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.menu.presentation.dto.request.CreateMealRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MainMenuRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealCreateResult;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;

import java.sql.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH;

@SpringBootTest
@DisplayName("MealService 테스트")
class MealServiceTest {

    @Autowired
    private MealService mealService;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealMenuRepository mealMenuRepository;

    @Autowired
    private MealMainMenuRepository mealMainMenuRepository;

    @Autowired
    private MenuRepository menuRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        mealMainMenuRepository.deleteAll();
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
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
        CreateMealRequest request = new CreateMealRequest(List.of("돈까스", "샐러드", "김치"), null);

        // then
        return mealService.createMeal(date, restaurant, timePart, request).mealId();
    }

    @Test
    void 식단을_조회한다() {
        // given
        Long meadId = 식단_생성_요청();

        // when
        MenusInMealResponse response = mealService.getMenusInMealByMealId(meadId, Language.KO);

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

    @Test
    void createMealWithPriceSavesGivenPrice() {
        // given
        Date date = Date.valueOf("2024-01-03");
        MealCreateWithPriceRequest request = new MealCreateWithPriceRequest(List.of("돈까스"), 4500, null);

        // when
        MealCreateResult result = mealService.createMealWithPrice(date, Restaurant.HAKSIK, LUNCH, request);

        // then
        Meal meal = mealRepository.findById(result.mealId()).orElseThrow();
        assertThat(meal.getPrice()).isEqualTo(4500);
    }

    @Test
    void createMealReturnsExistingMealIdWhenSameMenuSetAlreadyExists() {
        // given
        Date date = Date.valueOf("2024-01-03");
        CreateMealRequest request = new CreateMealRequest(List.of("돈까스", "샐러드"), null);
        Long firstMealId = mealService.createMeal(date, Restaurant.HAKSIK, LUNCH, request).mealId();

        // when
        Long secondMealId = mealService.createMeal(date, Restaurant.HAKSIK, LUNCH, request).mealId();

        // then
        assertThat(secondMealId).isEqualTo(firstMealId);
        assertThat(mealRepository.findAll()).hasSize(1);
    }

    @Test
    void createMealWithMainMenusSavesMatchedAndReportsUnmatchedNames() {
        // given
        Date date = Date.valueOf("2024-01-03");
        CreateMealRequest request = new CreateMealRequest(List.of("돈까스", "샐러드"),
                List.of(new MainMenuRequest("돈까스", "Pork Cutlet"), new MainMenuRequest("김치", "Kimchi")));

        // when
        MealCreateResult result = mealService.createMeal(date, Restaurant.HAKSIK, LUNCH, request);

        // then
        assertThat(result.unmatchedMainMenus()).containsExactly("김치");
        assertThat(mealService.getMenusInMealByMealId(result.mealId(), Language.EN).getBriefMenus())
                .extracting("name").contains("Pork Cutlet");
    }

    @Test
    void createMealWithEmptyMainMenusReturnsEmptyUnmatchedList() {
        // given
        Date date = Date.valueOf("2024-01-03");
        CreateMealRequest request = new CreateMealRequest(List.of("돈까스"), List.of());

        // when
        MealCreateResult result = mealService.createMeal(date, Restaurant.HAKSIK, LUNCH, request);

        // then
        assertThat(result.unmatchedMainMenus()).isEmpty();
    }

    @Test
    void 메뉴가_없는_식단을_조회하면_빈_목록을_반환한다() {
        // given
        Meal meal = mealRepository.save(new Meal(Date.valueOf("2024-01-03"), LUNCH, Restaurant.HAKSIK));

        // when
        MenusInMealResponse response = mealService.getMenusInMealByMealId(meal.getId(), Language.KO);

        // then
        assertThat(response.getBriefMenus()).isEmpty();
    }

    @Test
    void 변동식당_식단_상세를_조회한다() {
        // given
        Long mealId = 식단_생성_요청();

        // when
        List<MealDetailResponse> responses = mealService.getMealDetailsByDateAndRestaurantAndTimePart(
                Date.valueOf("2024-01-03"), Restaurant.HAKSIK, LUNCH, Language.KO);

        // then
        assertThat(responses).extracting(MealDetailResponse::getMealId).containsExactly(mealId);
    }

    @Test
    void 다른_식단에서_사용중인_메뉴는_삭제하지_않는다() {
        // given
        Date date = Date.valueOf("2024-01-03");
        CreateMealRequest request = new CreateMealRequest(List.of("돈까스"), null);
        Long sharedMealId = mealService.createMeal(date, Restaurant.HAKSIK, LUNCH, request).mealId();
        Long otherMealId = mealService.createMeal(date, Restaurant.HAKSIK, TimePart.DINNER, request).mealId();

        // when
        mealService.deleteByMealId(sharedMealId);

        // then
        assertThat(mealRepository.findById(otherMealId)).isPresent();
        assertThat(menuRepository.findAll()).extracting("name").contains("돈까스");
    }
}