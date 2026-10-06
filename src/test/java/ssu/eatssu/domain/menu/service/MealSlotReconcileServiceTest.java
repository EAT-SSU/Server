package ssu.eatssu.domain.menu.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMainMenu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMainMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.menu.presentation.dto.request.MainMenuRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealSlotReconcileResult;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MealSlotReconcileServiceTest {

    private static final Date DATE = Date.valueOf("2026-01-01");
    private static final Restaurant RESTAURANT = Restaurant.HAKSIK;
    private static final TimePart TIME_PART = TimePart.LUNCH;

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

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        reviewRepository.deleteAll();
        mealMainMenuRepository.deleteAll();
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void createsEveryMealInAnEmptySlot() {
        // given
        List<MealCreateWithPriceRequest> requests = List.of(
                request(List.of("돈까스", "김치"), 5000, List.of()),
                request(List.of("제육볶음", "된장국"), 5500, List.of()));

        // when
        MealSlotReconcileResult result = mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART, requests);

        // then
        assertThat(result.mealIds()).hasSize(2).doesNotHaveDuplicates();
        assertThat(result.unmatchedMainMenus()).containsExactly(List.of(), List.of());
        assertThat(result.deletedMealIds()).isEmpty();
        assertThat(result.keptWithReviews()).isEmpty();
        assertThat(mealRepository.findAll()).hasSize(2);
    }

    @Test
    void keepsIdenticalMealAndUpdatesItsPriceWithoutCreatingRows() {
        // given
        Long mealId = createMeal(request(List.of("돈까스", "김치"), 5000, null));
        long mealMenuCount = mealMenuRepository.count();

        // when
        MealSlotReconcileResult result = mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART,
                List.of(request(List.of("김치", "돈까스"), 6500, null)));

        // then
        assertThat(result.mealIds()).containsExactly(mealId);
        assertThat(mealRepository.count()).isOne();
        assertThat(mealMenuRepository.count()).isEqualTo(mealMenuCount);
        assertThat(mealRepository.findById(mealId).orElseThrow().getPrice()).isEqualTo(6500);
    }

    @Test
    void replacesChangedMealAndKeepsMenuRows() {
        // given
        Long oldMealId = createMeal(request(List.of("돈까스"), 5000, null));

        // when
        MealSlotReconcileResult result = mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART,
                List.of(request(List.of("제육볶음"), 5500, null)));

        // then
        assertThat(result.deletedMealIds()).containsExactly(oldMealId);
        assertThat(result.mealIds()).hasSize(1).doesNotContain(oldMealId);
        assertThat(mealRepository.findById(oldMealId)).isEmpty();
        assertThat(menuRepository.findAll()).extracting("name").containsExactlyInAnyOrder("돈까스", "제육볶음");
    }

    @Test
    void keepsUnmatchedMealWhenItHasReview() {
        // given
        Long reviewedMealId = createMeal(request(List.of("돈까스"), 5000, null));
        Meal reviewedMeal = mealRepository.findById(reviewedMealId).orElseThrow();
        reviewRepository.save(Review.builder().meal(reviewedMeal).rating(5).content("맛있어요").build());

        // when
        MealSlotReconcileResult result = mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART,
                List.of(request(List.of("제육볶음"), 5500, null)));

        // then
        assertThat(result.keptWithReviews()).containsExactly(reviewedMealId);
        assertThat(result.deletedMealIds()).isEmpty();
        assertThat(mealRepository.findById(reviewedMealId)).isPresent();
        assertThat(mealRepository.count()).isEqualTo(2);
    }

    @Test
    void nullMainMenusKeepsExistingRowsAndEmptyMainMenusClearsThem() {
        // given
        Long mealId = createMeal(request(List.of("돈까스"), 5000,
                List.of(new MainMenuRequest("돈까스", "Pork Cutlet"))));

        // when
        mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART,
                List.of(request(List.of("돈까스"), 5000, null)));

        // then
        assertThat(mealMainMenuRepository.findAllByMeal_Id(mealId))
                .extracting(MealMainMenu::getNameKo)
                .containsExactly("돈까스");

        // when
        mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART,
                List.of(request(List.of("돈까스"), 5000, List.of())));

        // then
        assertThat(mealMainMenuRepository.findAllByMeal_Id(mealId)).isEmpty();
    }

    @Test
    void returnsUnmatchedMainMenusForEachRequestInOrder() {
        // given
        List<MealCreateWithPriceRequest> requests = List.of(
                request(List.of("돈까스"), 5000,
                        List.of(new MainMenuRequest("없는 메뉴", "Unknown"))),
                request(List.of("제육볶음"), 5500,
                        List.of(new MainMenuRequest("제육볶음", "Spicy Pork"))));

        // when
        MealSlotReconcileResult result = mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART, requests);

        // then
        assertThat(result.unmatchedMainMenus()).containsExactly(List.of("없는 메뉴"), List.of());
    }

    @Test
    void rejectsDuplicateMealsRegardlessOfMenuOrder() {
        // given
        List<MealCreateWithPriceRequest> requests = List.of(
                request(List.of("돈까스", "김치"), 5000, null),
                request(List.of("김치", "돈까스"), 5500, null));

        // when & then
        assertThatThrownBy(() -> mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART, requests))
                .isInstanceOf(BaseException.class);
        assertThat(mealRepository.findAll()).isEmpty();
    }

    @Test
    void rejectsEmptyOrBlankMenuNames() {
        // given
        List<List<MealCreateWithPriceRequest>> invalidRequests = List.of(
                List.of(),
                List.of(request(List.of(), 5000, null)),
                List.of(request(List.of("   "), 5000, null)),
                List.of(request(Arrays.asList("돈까스", null), 5000, null)));

        // when & then
        for (List<MealCreateWithPriceRequest> requests : invalidRequests) {
            assertThatThrownBy(() -> mealService.reconcileMealSlot(DATE, RESTAURANT, TIME_PART, requests))
                    .isInstanceOf(BaseException.class);
        }
    }

    @Test
    void rejectsFixedRestaurant() {
        // given
        List<MealCreateWithPriceRequest> requests = List.of(request(List.of("돈까스"), 5000, null));

        // when & then
        assertThatThrownBy(() -> mealService.reconcileMealSlot(DATE, Restaurant.FOOD_COURT, TIME_PART, requests))
                .isInstanceOf(BaseException.class);
    }

    private Long createMeal(MealCreateWithPriceRequest request) {
        return mealService.createMealWithPrice(DATE, RESTAURANT, TIME_PART, request).mealId();
    }

    private MealCreateWithPriceRequest request(List<String> menuNames, Integer price,
                                               List<MainMenuRequest> mainMenus) {
        return new MealCreateWithPriceRequest(menuNames, price, mainMenus);
    }
}
