package ssu.eatssu.domain.review.dto.request;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReviewRequestDtoTest {

    private final User user = mock(User.class);
    private final Menu menu = mock(Menu.class);

    @Test
    void reviewCreateAndUploadRequestsMapRatingsToReview() {
        Review createReview = new ReviewCreateRequest(5, "좋아요").toEntity(user, menu);
        Review uploadReview = new UploadReviewRequest(4, "업로드").toReviewEntity(user, menu);

        assertThat(createReview.getUser()).isSameAs(user);
        assertThat(createReview.getMenu()).isSameAs(menu);
        assertThat(createReview.getRatings().getMainRating()).isEqualTo(5);
        assertThat(uploadReview.getRatings().getMainRating()).isEqualTo(4);
    }

    @Test
    void menuReviewRequestsMapToMenuReview() {
        CreateMenuReviewRequest request = new CreateMenuReviewRequest();
        request.setMainRating(3);
        request.setAmountRating(2);
        request.setTasteRating(1);
        request.setContent("메뉴 리뷰");

        Review v1Review = request.toReviewEntity(user, menu);
        Review v2Review = new CreateMenuReviewRequestV2(5, null, "V2 메뉴 리뷰", List.of())
                .toReviewEntity(user, menu);

        assertThat(v1Review.getRatings().getTasteRating()).isEqualTo(1);
        assertThat(v1Review.getContent()).isEqualTo("메뉴 리뷰");
        assertThat(v2Review.getRating()).isEqualTo(5);
        assertThat(v2Review.getMenu()).isSameAs(menu);
    }

    @Test
    void mealReviewRequestMapsMealAndImages() {
        ssu.eatssu.domain.menu.entity.Meal meal = mock(ssu.eatssu.domain.menu.entity.Meal.class);
        CreateMealReviewRequest request = new CreateMealReviewRequest(1L, 4, null, "식단 리뷰", List.of("image"));

        Review review = request.toReviewEntity(user, meal);

        assertThat(review.getMeal()).isSameAs(meal);
        assertThat(review.getRating()).isEqualTo(4);
        assertThat(request.createReviewImages(review)).extracting("imageUrl").containsExactly("image");
    }
}
