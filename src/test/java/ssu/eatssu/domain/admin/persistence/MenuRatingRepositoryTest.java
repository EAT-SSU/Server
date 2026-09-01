package ssu.eatssu.domain.admin.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.report.repository.ReportRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MenuRatingRepositoryTest {

    @Autowired
    private MenuRatingRepository menuRatingRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportRepository reportRepository;

    @BeforeEach
    void setup() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        reportRepository.deleteAll();
        reviewRepository.deleteAll();
        menuRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void getMainRatingAverageReturnsAverageOfReviewsForMenu() {
        User user = userRepository.save(User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234"));
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(4, 4, 4)).user(user).menu(menu).build());
        reviewRepository.save(Review.builder().content("리뷰").ratings(Ratings.of(2, 2, 2)).user(user).menu(menu).build());

        Double average = menuRatingRepository.getMainRatingAverage(menu.getId());

        assertThat(average).isEqualTo(3.0);
    }

    @Test
    void getMainRatingAverageReturnsNullWhenMenuHasNoReviews() {
        Menu menu = menuRepository.save(Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null));

        Double average = menuRatingRepository.getMainRatingAverage(menu.getId());

        assertThat(average).isNull();
    }
}
