package ssu.eatssu.domain.review.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import ssu.eatssu.domain.menu.entity.constants.MenuType;
import ssu.eatssu.domain.review.dto.request.ReviewCreateRequest;
import ssu.eatssu.domain.review.dto.request.ReviewUpdateRequest;
import ssu.eatssu.domain.review.dto.request.UploadReviewRequest;
import ssu.eatssu.domain.review.service.ReviewService;
import ssu.eatssu.domain.slice.service.SliceService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReviewControllerDirectTest {

    @Test
    void delegatesAllV1ReviewEndpoints() {
        ReviewService reviewService = mock(ReviewService.class);
        SliceService sliceService = mock(SliceService.class);
        ReviewController controller = new ReviewController(reviewService, sliceService);
        ReviewCreateRequest createRequest = new ReviewCreateRequest(5, "좋아요");
        UploadReviewRequest uploadRequest = new UploadReviewRequest(4, "업로드");
        ReviewUpdateRequest updateRequest = new ReviewUpdateRequest(3, null, null, "수정");
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        PageRequest pageable = PageRequest.of(0, 20);

        assertThat(controller.getReviews(MenuType.FIXED, 1L, null, null, pageable, null).getIsSuccess()).isTrue();
        assertThat(controller.writeReview(1L, createRequest, List.of(image), null).getIsSuccess()).isTrue();
        assertThat(controller.uploadReviewImage(image).getIsSuccess()).isTrue();
        assertThat(controller.writeReview(1L, uploadRequest, null).getIsSuccess()).isTrue();
        assertThat(controller.updateReview(1L, updateRequest, null).getIsSuccess()).isTrue();
        assertThat(controller.deleteReview(1L, null).getIsSuccess()).isTrue();
        assertThat(controller.getMealReviews(1L).getIsSuccess()).isTrue();
        assertThat(controller.getMainReviews(1L).getIsSuccess()).isTrue();

        verify(sliceService).findReviewsV1(MenuType.FIXED, 1L, null, pageable, null, null);
        verify(reviewService).createReview(null, 1L, createRequest, List.of(image));
        verify(reviewService).uploadImage(image);
        verify(reviewService).uploadReview(null, 1L, uploadRequest);
        verify(reviewService).updateReview(null, 1L, updateRequest);
        verify(reviewService).deleteReview(null, 1L);
        verify(reviewService).findMealReviews(1L);
        verify(reviewService).findMenuReviews(1L);
    }
}
