package ssu.eatssu.domain.menu.persistence;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class QuerydslMenuRatingCalculatorTest {

    @Autowired
    private QuerydslMenuRatingCalculator calculator;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getMainRatingAverageReturnsAverageOfReviewsForMenu() {
        // given
        User user = userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));
        saveReviewWithRating(user, menu, 4);
        saveReviewWithRating(user, menu, 2);

        // when
        Double average = calculator.getMainRatingAverage(menu.getId());

        // then
        assertThat(average).isEqualTo(3.0);
    }

    @Test
    void getMainRatingAverageReturnsNullWhenMenuHasNoReviews() {
        // given
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));

        // when
        Double average = calculator.getMainRatingAverage(menu.getId());

        // then
        assertThat(average).isNull();
    }

    private void saveReviewWithRating(User user, Menu menu, int mainRating) {
        reviewRepository.save(Review.builder()
                                    .content("리뷰")
                                    .ratings(Ratings.of(mainRating, mainRating, mainRating))
                                    .user(user)
                                    .menu(menu)
                                    .build());
    }
}
