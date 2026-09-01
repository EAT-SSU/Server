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
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuerydslMealRatingCounterTest {

    @Autowired
    private QuerydslMealRatingCounter counter;

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
    void getRatingCountMapAndTotalCountReflectReviewsForMenusInMeal() {
        // given
        User user = userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));
        Menu menu = menuRepository.save(Menu.createVariable("제육볶음", Restaurant.DODAM));
        mealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(4, 4, 4)).user(user).menu(menu).build());
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(4, 4, 4)).user(user).menu(menu).build());
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(2, 2, 2)).user(user).menu(menu).build());

        // when
        Map<Integer, Long> ratingCountMap = counter.getRatingCountMap(meal.getId());
        Long totalCount = counter.getTotalRatingCount(meal.getId());

        // then
        assertThat(ratingCountMap).containsEntry(4, 2L).containsEntry(2, 1L);
        assertThat(totalCount).isEqualTo(3L);
    }

    @Test
    void getRatingCountMapAndTotalCountReturnEmptyWhenMealHasNoMenus() {
        // given
        Meal meal = mealRepository.save(new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM));

        // when
        Map<Integer, Long> ratingCountMap = counter.getRatingCountMap(meal.getId());
        Long totalCount = counter.getTotalRatingCount(meal.getId());

        // then
        assertThat(ratingCountMap).isEmpty();
        assertThat(totalCount).isZero();
    }

    @Test
    void getRatingCountMapAndTotalCountReturnEmptyWhenMenuIdsIsNull() {
        // given
        MealMenuQueryRepository nullReturningRepository = org.mockito.Mockito.mock(MealMenuQueryRepository.class);
        org.mockito.BDDMockito.given(nullReturningRepository.getMenuIds(1L)).willReturn(null);
        QuerydslMealRatingCounter counterWithNullMenuIds =
                new QuerydslMealRatingCounter(nullReturningRepository, queryFactory);

        // when
        Map<Integer, Long> ratingCountMap = counterWithNullMenuIds.getRatingCountMap(1L);
        Long totalCount = counterWithNullMenuIds.getTotalRatingCount(1L);

        // then
        assertThat(ratingCountMap).isEmpty();
        assertThat(totalCount).isZero();
    }
}
