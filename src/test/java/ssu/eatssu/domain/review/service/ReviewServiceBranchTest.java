package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.rating.entity.RatingCalculator;
import ssu.eatssu.domain.review.dto.RatingAverages;
import ssu.eatssu.domain.review.dto.ReviewRatingCount;
import ssu.eatssu.domain.review.dto.request.ReviewCreateRequest;
import ssu.eatssu.domain.review.dto.request.ReviewUpdateRequest;
import ssu.eatssu.domain.review.dto.request.UploadReviewRequest;
import ssu.eatssu.domain.review.dto.response.MealReviewsResponse;
import ssu.eatssu.domain.review.dto.response.MenuReviewResponse;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class ReviewServiceBranchTest {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);
    private final ReviewImageRepository reviewImageRepository = mock(ReviewImageRepository.class);
    private final ReviewTranslationRepository reviewTranslationRepository = mock(ReviewTranslationRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MealRepository mealRepository = mock(MealRepository.class);
    private final RatingCalculator ratingCalculator = mock(RatingCalculator.class);
    private final S3Uploader s3Uploader = mock(S3Uploader.class);
    private final ReviewService reviewService = new ReviewService(userRepository, reviewRepository,
            reviewImageRepository, reviewTranslationRepository, menuRepository, mealRepository, ratingCalculator,
            s3Uploader);

    @Test
    void uploadImageReturnsStoredUrl() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        given(s3Uploader.upload(image, "reviewImg")).willReturn("https://cdn/review.png");

        assertThat(reviewService.uploadImage(image).getUrl()).isEqualTo("https://cdn/review.png");
    }

    @Test
    void uploadImageConvertsIOExceptionToBaseException() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        given(s3Uploader.upload(image, "reviewImg")).willThrow(new IOException());

        assertThatThrownBy(() -> reviewService.uploadImage(image)).isInstanceOf(BaseException.class);
    }

    @Test
    void createReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(userDetails(), 1L, new ReviewCreateRequest(4, "굿"), null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createReviewThrowsWhenMenuDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(userDetails(), 1L, new ReviewCreateRequest(4, "굿"), null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createReviewWithEmptyImageListSkipsProcessing() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Menu menu = Menu.createVariable("돈까스", ssu.eatssu.domain.restaurant.entity.Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        reviewService.createReview(userDetails(), 1L, new ReviewCreateRequest(4, "굿"), List.of());

        verify(reviewImageRepository, never()).save(any());
    }

    @Test
    void createReviewSkipsEmptyImageFile() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Menu menu = Menu.createVariable("돈까스", ssu.eatssu.domain.restaurant.entity.Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        MockMultipartFile emptyImage = new MockMultipartFile("image", "empty.png", "image/png", new byte[0]);

        reviewService.createReview(userDetails(), 1L, new ReviewCreateRequest(4, "굿"), List.of(emptyImage));

        verify(reviewImageRepository, never()).save(any());
    }

    @Test
    void createReviewProcessesNonEmptyImageAndSavesReviewImage() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Menu menu = Menu.createVariable("돈까스", ssu.eatssu.domain.restaurant.entity.Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        given(s3Uploader.upload(image, "reviewImg")).willReturn("https://cdn/review.png");

        reviewService.createReview(userDetails(), 1L, new ReviewCreateRequest(4, "굿"), List.of(image));

        verify(reviewImageRepository).save(any());
    }

    @Test
    void createReviewThrowsWhenImageUploadFails() throws Exception {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Menu menu = Menu.createVariable("돈까스", ssu.eatssu.domain.restaurant.entity.Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        MockMultipartFile image = new MockMultipartFile("image", "review.png", "image/png", new byte[]{1});
        given(s3Uploader.upload(image, "reviewImg")).willThrow(new IOException());

        assertThatThrownBy(() -> reviewService.createReview(userDetails(), 1L, new ReviewCreateRequest(4, "굿"),
                                                             List.of(image)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void uploadReviewSavesReviewAndImage() {
        User user = mock(User.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        Menu menu = Menu.createVariable("돈까스", ssu.eatssu.domain.restaurant.entity.Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        UploadReviewRequest request = uploadReviewRequest("https://cdn/review.png");

        reviewService.uploadReview(userDetails(), 1L, request);

        verify(reviewRepository).save(any());
        verify(reviewImageRepository).save(any());
    }

    @Test
    void uploadReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.uploadReview(userDetails(), 1L,
                                                             uploadReviewRequest("url")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void uploadReviewThrowsWhenMenuDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.uploadReview(userDetails(), 1L,
                                                             uploadReviewRequest("url")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(userDetails(), 2L,
                                                             new ReviewUpdateRequest(5, 5, 5, "변경")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewThrowsWhenReviewDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(reviewRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(userDetails(), 2L,
                                                             new ReviewUpdateRequest(5, 5, 5, "변경")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewRejectsNonAuthor() {
        User requester = mock(User.class);
        User author = mock(User.class);
        Review review = Review.builder().user(author).content("original").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.updateReview(userDetails(), 2L,
                                                             new ReviewUpdateRequest(5, 5, 5, "changed")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewDeletesTranslationsWhenContentChanges() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().id(2L).user(author).content("original").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        reviewService.updateReview(userDetails(), 2L, new ReviewUpdateRequest(5, 5, 5, "changed"));

        verify(reviewTranslationRepository).deleteAllByReview_Id(2L);
    }

    @Test
    void updateReviewKeepsTranslationsWhenContentUnchanged() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().id(2L).user(author).content("동일").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        reviewService.updateReview(userDetails(), 2L, new ReviewUpdateRequest(5, 5, 5, "동일"));

        verify(reviewTranslationRepository, never()).deleteAllByReview_Id(any());
    }

    @Test
    void deleteReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(userDetails(), 2L)).isInstanceOf(BaseException.class);
    }

    @Test
    void deleteReviewThrowsWhenReviewDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(reviewRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.deleteReview(userDetails(), 2L)).isInstanceOf(BaseException.class);
    }

    @Test
    void deleteReviewRejectsNonAuthor() {
        User requester = mock(User.class);
        User author = mock(User.class);
        Review review = Review.builder().user(author).content("굿").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewService.deleteReview(userDetails(), 2L)).isInstanceOf(BaseException.class);
    }

    @Test
    void deleteReviewRemovesReviewWrittenByAuthor() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().user(author).content("굿").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        reviewService.deleteReview(userDetails(), 2L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void findMenuReviewsReturnsAggregatedRatings() {
        Menu menu = Menu.createFixed("돈까스", ssu.eatssu.domain.restaurant.entity.Restaurant.FOOD_COURT, 6000, null);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(ratingCalculator.menuAverageRatings(menu)).willReturn(new RatingAverages(4.5));
        given(ratingCalculator.menuRatingCount(menu)).willReturn(new ReviewRatingCount(0, 0, 0, 1, 1));

        MenuReviewResponse response = reviewService.findMenuReviews(1L);

        assertThat(response.getMenuName()).isEqualTo("돈까스");
        assertThat(response.getMainRating()).isEqualTo(4.5);
    }

    @Test
    void findMenuReviewsThrowsWhenMenuDoesNotExist() {
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.findMenuReviews(1L)).isInstanceOf(BaseException.class);
    }

    @Test
    void findMealReviewsReturnsAggregatedRatings() {
        Meal meal = mock(Meal.class);
        given(meal.getMenuNames()).willReturn(List.of("돈까스"));
        given(mealRepository.findById(1L)).willReturn(Optional.of(meal));
        given(ratingCalculator.mealTotalReviewCount(meal)).willReturn(2L);
        given(ratingCalculator.mealAverageRatings(meal)).willReturn(new RatingAverages(4.5));
        given(ratingCalculator.mealRatingCount(meal)).willReturn(new ReviewRatingCount(0, 0, 0, 1, 1));

        MealReviewsResponse response = reviewService.findMealReviews(1L);

        assertThat(response.getMenuNames()).containsExactly("돈까스");
        assertThat(response.getTotalReviewCount()).isEqualTo(2L);
    }

    @Test
    void findMealReviewsThrowsWhenMealDoesNotExist() {
        given(mealRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.findMealReviews(1L)).isInstanceOf(BaseException.class);
    }

    private UploadReviewRequest uploadReviewRequest(String imageUrl) {
        UploadReviewRequest request = new UploadReviewRequest(4, "굿");
        request.setImageUrl(imageUrl);
        return request;
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
