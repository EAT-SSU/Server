package ssu.eatssu.domain.review.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReviewRepositoryImplTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MealRepository mealRepository;

    @Autowired
    private MealMenuRepository mealMenuRepository;

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
    void findAllByMenuOrderByIdDescHasNextTrueWhenMoreReviewsExist() {
        // given
        User user = createUser();
        Menu menu = createMenu();
        createReview(user, menu, "첫번째");
        createReview(user, menu, "두번째");

        // when
        Slice<Review> slice = reviewRepository.findAllByMenuOrderByIdDesc(menu, null, PageRequest.of(0, 1));

        // then
        assertThat(slice.hasNext()).isTrue();
        assertThat(slice.getContent()).hasSize(1);
        assertThat(slice.getContent().get(0).getContent()).isEqualTo("두번째");
    }

    @Test
    void findAllByMenuOrderByIdDescOnlyReturnsReviewsBeforeLastReviewId() {
        // given
        User user = createUser();
        Menu menu = createMenu();
        Review older = createReview(user, menu, "첫번째");
        createReview(user, menu, "두번째");

        // when
        Slice<Review> slice = reviewRepository.findAllByMenuOrderByIdDesc(menu, older.getId() + 1,
                                                                          PageRequest.of(0, 10));

        // then
        assertThat(slice.hasNext()).isFalse();
        assertThat(slice.getContent()).extracting(Review::getContent).containsExactly("첫번째");
    }

    @Test
    void findAllByMenuOrderByIdAscReturnsOldestFirstWhenLastReviewIdIsNull() {
        // given
        User user = createUser();
        Menu menu = createMenu();
        createReview(user, menu, "첫번째");
        createReview(user, menu, "두번째");

        // when
        Slice<Review> slice = reviewRepository.findAllByMenuOrderByIdAsc(menu, null, PageRequest.of(0, 10));

        // then
        assertThat(slice.getContent()).extracting(Review::getContent).containsExactly("첫번째", "두번째");
    }

    @Test
    void findAllByMenuOrderByIdAscOnlyReturnsReviewsAfterLastReviewId() {
        // given
        User user = createUser();
        Menu menu = createMenu();
        Review older = createReview(user, menu, "첫번째");
        Review newer = createReview(user, menu, "두번째");

        // when
        Slice<Review> slice = reviewRepository.findAllByMenuOrderByIdAsc(menu, older.getId(), PageRequest.of(0, 10));

        // then
        assertThat(slice.getContent()).extracting(Review::getId).containsExactly(newer.getId());
    }

    @Test
    void findAllByMealOrderByIdDescReturnsReviewsForMenusInMeal() {
        // given
        User user = createUser();
        Menu menu = createMenu();
        Meal meal = createMeal(menu);
        createReview(user, menu, "식단리뷰");

        // when
        Slice<Review> slice = reviewRepository.findAllByMealOrderByIdDesc(meal, null, PageRequest.of(0, 10));

        // then
        assertThat(slice.getContent()).extracting(Review::getContent).containsExactly("식단리뷰");
    }

    @Test
    void findAllByMealOrderByIdAscReturnsReviewsForMenusInMeal() {
        // given
        User user = createUser();
        Menu menu = createMenu();
        Meal meal = createMeal(menu);
        createReview(user, menu, "식단리뷰");

        // when
        Slice<Review> slice = reviewRepository.findAllByMealOrderByIdAsc(meal, null, PageRequest.of(0, 10));

        // then
        assertThat(slice.getContent()).extracting(Review::getContent).containsExactly("식단리뷰");
    }

    @Test
    void findByUserOrderByIdDescReturnsOnlyThatUsersReviews() {
        // given
        User user = createUser();
        User otherUser = createUser("other@test.com", "other-user");
        Menu menu = createMenu();
        createReview(otherUser, menu, "다른유저리뷰");
        Review myReview = createReview(user, menu, "내리뷰");

        // when
        Slice<Review> slice = reviewRepository.findByUserOrderByIdDesc(user, null, PageRequest.of(0, 10));

        // then
        assertThat(slice.getContent()).extracting(Review::getId).containsExactly(myReview.getId());
    }

    private User createUser() {
        return createUser("test@test.com", "user-test");
    }

    private User createUser(String email, String nickname) {
        return userRepository.save(User.create(email, nickname, OAuthProvider.EATSSU, "1234", "1234"));
    }

    private Menu createMenu() {
        return menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));
    }

    private Meal createMeal(Menu menu) {
        Meal meal = new Meal(new Date(), TimePart.LUNCH, Restaurant.DODAM);
        meal.addMealMenu(MealMenu.builder().meal(meal).menu(menu).build());
        return mealRepository.save(meal);
    }

    private Review createReview(User user, Menu menu, String content) {
        Review review = Review.builder()
                              .content(content)
                              .rating(4)
                              .user(user)
                              .menu(menu)
                              .build();
        return reviewRepository.save(review);
    }
}
