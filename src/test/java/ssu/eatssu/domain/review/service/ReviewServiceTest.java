package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.ReviewCreateRequest;
import ssu.eatssu.domain.review.dto.ReviewUpdateRequest;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @BeforeEach
    void setup() {
        reviewRepository.deleteAll();
        userRepository.deleteAll();
        menuRepository.deleteAll();
    }

    @Test
    void 리뷰를_생성한다() {
        User user = createUser();
        CustomUserDetails userDetails = createCustomUserDetails(user);
        Review createdReview = 리뷰_생성_요청(userDetails);

        // then
        assertThat(reviewRepository.findAll()).hasSize(1);
        assertThat(reviewRepository.findById(createdReview.getId()).orElseThrow().getContent())
                .isEqualTo("굿");
    }

    @Test
    void 리뷰를_업데이트한다() {
        // given
        User user = createUser();
        CustomUserDetails userDetails = createCustomUserDetails(user);
        Review createdReview = 리뷰_생성_요청(userDetails);

        // when
        ReviewUpdateRequest request = new ReviewUpdateRequest(5, 5, 5, "굿굿");
        reviewService.updateReview(userDetails, createdReview.getId(), request);

        // then
        assertThat(reviewRepository.findById(createdReview.getId()).orElseThrow().getContent())
                .isEqualTo("굿굿");
    }

    @Test
    void 리뷰를_삭제한다() {
        // given
        User user = createUser();
        CustomUserDetails userDetails = createCustomUserDetails(user);
        Review createdReview = 리뷰_생성_요청(userDetails);

        // when
        reviewService.deleteReview(userDetails, createdReview.getId());

        // then
        assertThat(reviewRepository.findAll()).hasSize(0);
    }

    private Review 리뷰_생성_요청(CustomUserDetails userDetails) {
        // given
        Menu menu = createMenu();
        ReviewCreateRequest request = new ReviewCreateRequest(4, "굿");

        // when
        Review createdReview = reviewService.createReview(userDetails, menu.getId(),
                                                          request, null);
        return createdReview;
    }

    private User createUser() {
        return User.create("test@test.com", "user-test", OAuthProvider.EATSSU, "1234", "1234");
    }

    private CustomUserDetails createCustomUserDetails(User user) {
        User savedUser = userRepository.save(user);
        return new CustomUserDetails(savedUser);
    }

    private Menu createMenu() {
        Menu menu = Menu.createFixed("라면", Restaurant.FOOD_COURT, 3000, null);
        return menuRepository.save(menu);
    }
}