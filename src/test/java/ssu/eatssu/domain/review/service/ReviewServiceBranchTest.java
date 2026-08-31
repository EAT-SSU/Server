package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.rating.entity.RatingCalculator;
import ssu.eatssu.domain.review.dto.request.ReviewUpdateRequest;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewImageRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.repository.ReviewTranslationRepository;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.util.S3Uploader;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class ReviewServiceBranchTest {

    @Test
    void uploadImageReturnsStoredUrl() throws Exception {
        S3Uploader uploader = mock(S3Uploader.class);
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        given(uploader.upload(image, "reviewImg")).willReturn("https://cdn/review.png");

        assertThat(service(mock(UserRepository.class), mock(ReviewRepository.class), uploader).uploadImage(image).getUrl())
                .isEqualTo("https://cdn/review.png");
    }

    @Test
    void uploadImageConvertsIOExceptionToBaseException() throws Exception {
        S3Uploader uploader = mock(S3Uploader.class);
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        given(uploader.upload(image, "reviewImg")).willThrow(new IOException());

        assertThatThrownBy(() -> service(mock(UserRepository.class), mock(ReviewRepository.class), uploader).uploadImage(image))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewRejectsNonAuthor() {
        UserRepository userRepository = mock(UserRepository.class);
        ReviewRepository reviewRepository = mock(ReviewRepository.class);
        User requester = mock(User.class);
        User author = mock(User.class);
        Review review = Review.builder().user(author).content("original").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> service(userRepository, reviewRepository, mock(S3Uploader.class))
                .updateReview(userDetails(), 2L, new ReviewUpdateRequest(5, 5, 5, "changed")))
                .isInstanceOf(BaseException.class);
    }

    private ReviewService service(UserRepository userRepository, ReviewRepository reviewRepository, S3Uploader uploader) {
        return new ReviewService(userRepository, reviewRepository, mock(ReviewImageRepository.class),
                mock(ReviewTranslationRepository.class), mock(MenuRepository.class), mock(MealRepository.class),
                mock(RatingCalculator.class), uploader);
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
