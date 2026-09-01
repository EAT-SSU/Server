package ssu.eatssu.domain.menu.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuerydslMealRatingCalculatorTest {

    @Autowired
    private QuerydslMealRatingCalculator calculator;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealMenuRepository mealMenuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JPAQueryFactory queryFactory;

    @BeforeEach
    void setup() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        reviewRepository.deleteAll();
        mealMenuRepository.deleteAll();
        mealRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getMainRatingAverageReturnsAverageOfReviewsForMenusInMeal() {
        // given
        User user = userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));
        Menu menu = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(4, 4, 4)).user(user).menu(menu).build());
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(2, 2, 2)).user(user).menu(menu).build());

        // when
        Double average = calculator.getMainRatingAverage(meal.getId());

        // then
        assertThat(average).isEqualTo(3.0);
    }

    @Test
    void getMainRatingAverageReturnsNullWhenMealHasNoMenus() {
        // given
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));

        // when
        Double average = calculator.getMainRatingAverage(meal.getId());

        // then
        assertThat(average).isNull();
    }

    @Test
    void getMainRatingAverageReturnsNullWhenMenuIdsIsNull() {
        // given
        MealMenuQueryRepository nullReturningRepository = org.mockito.Mockito.mock(MealMenuQueryRepository.class);
        org.mockito.BDDMockito.given(nullReturningRepository.getMenuIds(1L)).willReturn(null);
        QuerydslMealRatingCalculator calculatorWithNullMenuIds =
                new QuerydslMealRatingCalculator(nullReturningRepository, queryFactory);

        // when
        Double average = calculatorWithNullMenuIds.getMainRatingAverage(1L);

        // then
        assertThat(average).isNull();
    }
}
