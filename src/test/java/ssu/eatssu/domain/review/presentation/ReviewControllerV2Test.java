package ssu.eatssu.domain.review.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.request.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.request.CreateMenuReviewRequestV2;
import ssu.eatssu.domain.review.dto.request.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.review.service.ReviewTranslationService;
import ssu.eatssu.domain.user.entity.Language;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReviewControllerV2Test {

    @Test
    void delegatesAllV2ReviewEndpoints() {
        ReviewServiceV2 reviewService = mock(ReviewServiceV2.class);
        ReviewTranslationService translationService = mock(ReviewTranslationService.class);
        ReviewControllerV2 controller = new ReviewControllerV2(reviewService, translationService);
        CreateMealReviewRequest mealRequest = new CreateMealReviewRequest(1L, 5, null, "좋아요", null);
        CreateMenuReviewRequestV2 menuRequest = new CreateMenuReviewRequestV2(5, null, "좋아요", null);
        UpdateMealReviewRequest updateRequest = new UpdateMealReviewRequest(4, null, "수정");
        PageRequest pageable = PageRequest.of(0, 20);

        assertThat(controller.createMealReview(mealRequest, null).getIsSuccess()).isTrue();
        assertThat(controller.getRestaurantReviews(Restaurant.DODAM).getIsSuccess()).isTrue();
        assertThat(controller.getMealReviewList(1L, null, null).getIsSuccess()).isTrue();
        assertThat(controller.updateReview(1L, updateRequest, null).getIsSuccess()).isTrue();
        assertThat(controller.deleteReview(1L, null).getIsSuccess()).isTrue();
        assertThat(controller.translateReview(1L, Language.EN).getIsSuccess()).isTrue();
        assertThat(controller.getMealReviews(1L).getIsSuccess()).isTrue();
        assertThat(controller.getMainReviews(1L).getIsSuccess()).isTrue();
        assertThat(controller.getMenuReviewList(1L, null, pageable, null).getIsSuccess()).isTrue();
        assertThat(controller.createMenuReview(menuRequest, null).getIsSuccess()).isTrue();
        assertThat(controller.getMyReviews(null, pageable, null).getIsSuccess()).isTrue();
        assertThat(controller.getValidMenuForReview(1L).getIsSuccess()).isTrue();

        verify(reviewService).createMealReview(null, mealRequest);
        verify(reviewService).findRestaurantReviews(Restaurant.DODAM);
        verify(reviewService).updateReview(null, 1L, updateRequest);
        verify(reviewService).deleteReview(null, 1L);
        verify(translationService).translateReview(1L, Language.EN);
        verify(reviewService).createMenuReview(null, menuRequest);
        verify(reviewService).validMenuForReview(1L);
    }
}
