package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.menu.service.MealRatingService;
import ssu.eatssu.domain.rating.entity.Ratings;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.request.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.request.CreateMenuReviewRequestV2;
import ssu.eatssu.domain.review.dto.request.MenuLikeRequest;
import ssu.eatssu.domain.review.dto.request.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.response.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.response.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.response.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.response.ValidMenuForViewResponse;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.repository.ReviewTranslationRepository;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewServiceV2Test {

    private final UserRepository userRepository = mock(UserRepository.class);
    private final ReviewRepository reviewRepository = mock(ReviewRepository.class);
    private final ReviewTranslationRepository reviewTranslationRepository = mock(ReviewTranslationRepository.class);
    private final MenuRepository menuRepository = mock(MenuRepository.class);
    private final MealRepository mealRepository = mock(MealRepository.class);
    private final MealMenuRepository mealMenuRepository = mock(MealMenuRepository.class);
    private final ApplicationEventPublisher eventPublisher = mock(ApplicationEventPublisher.class);
    private final MealRatingService mealRatingService = mock(MealRatingService.class);
    private final ReviewServiceV2 reviewServiceV2 = new ReviewServiceV2(userRepository, reviewRepository,
            reviewTranslationRepository, menuRepository, mealRepository, mealMenuRepository, eventPublisher,
            mealRatingService);

    @Test
    void 존재하지_않는_식단의_리뷰는_조회하지_않는다() {
        given(mealRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.findMealReviewList(1L, null, PageRequest.of(0, 20), null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 리뷰_가능한_메뉴가_없으면_빈_목록을_반환한다() {
        Meal meal = mock(Meal.class);
        given(mealRepository.findById(1L)).willReturn(Optional.of(meal));
        given(mealMenuRepository.findMenusByMeal(meal)).willReturn(List.of());

        SliceResponse<?> response = reviewServiceV2.findMealReviewList(1L, null, PageRequest.of(0, 20), null);

        assertThat(response.getDataList()).isEmpty();
        assertThat(response.isHasNext()).isFalse();
    }

    @Test
    void createMealReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.createMealReview(userDetails(),
                new CreateMealReviewRequest(1L, 4, null, "굿", null)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createMealReviewThrowsWhenMealDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(mealRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.createMealReview(userDetails(),
                new CreateMealReviewRequest(1L, 4, null, "굿", null)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createMealReviewThrowsWhenMenuLikeHasNoMenuId() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Meal meal = new Meal(new java.util.Date(), ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH, Restaurant.DODAM);
        given(mealRepository.findById(1L)).willReturn(Optional.of(meal));

        assertThatThrownBy(() -> reviewServiceV2.createMealReview(userDetails(),
                new CreateMealReviewRequest(1L, 4, List.of(new MenuLikeRequest(null, true)), "굿", null)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createMealReviewSavesReviewWithImagesAndMenuLikes() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Meal meal = new Meal(new java.util.Date(), ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH, Restaurant.DODAM);
        given(mealRepository.findById(1L)).willReturn(Optional.of(meal));
        Menu menu = Menu.createVariable("돈까스", Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        reviewServiceV2.createMealReview(userDetails(), new CreateMealReviewRequest(1L, 4,
                List.of(new MenuLikeRequest(1L, true)), "굿", List.of("https://cdn/1.png")));

        verify(reviewRepository).save(any());
        verify(eventPublisher).publishEvent(any(Object.class));
    }

    @Test
    void createMenuReviewThrowsWhenMenuLikeIsMissing() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));

        assertThatThrownBy(() -> reviewServiceV2.createMenuReview(userDetails(),
                new CreateMenuReviewRequestV2(4, null, "굿", null)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createMenuReviewThrowsWhenMenuDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.createMenuReview(userDetails(),
                new CreateMenuReviewRequestV2(4, new MenuLikeRequest(1L, true), "굿", null)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void createMenuReviewSavesReviewAndAddsMenuLike() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        Menu menu = Menu.createVariable("돈까스", Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.save(any())).willAnswer(invocation -> invocation.getArgument(0));

        reviewServiceV2.createMenuReview(userDetails(), new CreateMenuReviewRequestV2(4,
                new MenuLikeRequest(1L, true), "굿", List.of("https://cdn/1.png")));

        verify(reviewRepository).save(any());
        assertThat(menu.getLikeCount()).isEqualTo(1);
    }

    @Test
    void findRestaurantReviewsAggregatesRatingsAndLikes() {
        Meal meal = new Meal(new java.util.Date(), ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH, Restaurant.DODAM);
        given(mealRepository.findByRestaurant(Restaurant.DODAM)).willReturn(List.of(meal));
        Menu menu = Menu.createVariable("돈까스", Restaurant.DODAM);
        menu.increaseLikeCount();
        Review review = Review.builder().ratings(Ratings.of(4, 4, 4)).menu(menu).build();
        given(reviewRepository.findByMealIn(List.of(meal))).willReturn(List.of(review));
        given(mealMenuRepository.findMenusByMeals(List.of(meal))).willReturn(List.of(menu));

        RestaurantReviewResponse response = reviewServiceV2.findRestaurantReviews(Restaurant.DODAM);

        assertThat(response.getTotalReviewCount()).isEqualTo(1);
        assertThat(response.getRating()).isEqualTo(4.0);
        assertThat(response.getLikeCount()).isEqualTo(1);
    }

    @Test
    void findMenuReviewListThrowsWhenMenuDoesNotExist() {
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.findMenuReviewList(1L, PageRequest.of(0, 20), null, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void findMenuReviewListReturnsSliceOfReviewDetails() {
        Menu menu = Menu.createVariable("돈까스", Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        Slice<Review> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        given(reviewRepository.findAllByMenuOrderByIdDesc(menu, null, PageRequest.of(0, 20))).willReturn(slice);

        SliceResponse<?> response = reviewServiceV2.findMenuReviewList(1L, PageRequest.of(0, 20), null, null);

        assertThat(response.getDataList()).isEmpty();
    }

    @Test
    void findMenuReviewsV2ThrowsWhenMenuDoesNotExist() {
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.findMenuReviews(1L)).isInstanceOf(BaseException.class);
    }

    @Test
    void findMenuReviewsV2ReturnsAggregatedResponse() {
        Menu menu = Menu.createVariable("돈까스", Restaurant.DODAM);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        Review review = Review.builder().ratings(Ratings.of(4, 4, 4)).menu(menu).build();
        given(reviewRepository.findAllByMenu(menu)).willReturn(List.of(review));

        MenuReviewsV2Response response = reviewServiceV2.findMenuReviews(1L);

        assertThat(response.getMenuName()).isEqualTo("돈까스");
        assertThat(response.getTotalReviewCount()).isEqualTo(1L);
        assertThat(response.getRating()).isEqualTo(4.0);
    }

    @Test
    void findMealReviewsV2ThrowsWhenMealDoesNotExist() {
        given(mealRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.findMealReviews(1L)).isInstanceOf(BaseException.class);
    }

    @Test
    void findMealReviewsV2ReturnsAggregatedResponse() {
        Meal meal = new Meal(new java.util.Date(), ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH, Restaurant.DODAM);
        given(mealRepository.findById(1L)).willReturn(Optional.of(meal));
        Menu menu = Menu.createVariable("돈까스", Restaurant.DODAM);
        Review review = Review.builder().ratings(Ratings.of(4, 4, 4)).meal(meal).build();
        given(reviewRepository.findAllMealAndMenuReviews(1L)).willReturn(List.of(review));
        given(mealMenuRepository.findMenusByMeal(meal)).willReturn(List.of(menu));

        MealReviewsV2Response response = reviewServiceV2.findMealReviews(1L);

        assertThat(response.getTotalReviewCount()).isEqualTo(1L);
        assertThat(response.getRating()).isEqualTo(4.0);
        assertThat(response.getMenuList()).extracting("name").containsExactly("돈까스");
    }

    @Test
    void updateReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.updateReview(userDetails(), 2L,
                new UpdateMealReviewRequest(5, null, "변경")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewThrowsWhenReviewDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(reviewRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.updateReview(userDetails(), 2L,
                new UpdateMealReviewRequest(5, null, "변경")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewRejectsNonAuthor() {
        User requester = mock(User.class);
        User author = mock(User.class);
        Review review = Review.builder().user(author).content("원본").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewServiceV2.updateReview(userDetails(), 2L,
                new UpdateMealReviewRequest(5, null, "변경")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewThrowsWhenMenuLikeHasNoMenuId() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().id(2L).user(author).content("원본").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewServiceV2.updateReview(userDetails(), 2L,
                new UpdateMealReviewRequest(5, List.of(new MenuLikeRequest(null, true)), "변경")))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateReviewChangesContentAndDeletesTranslations() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().id(2L).user(author).content("원본").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        reviewServiceV2.updateReview(userDetails(), 2L, new UpdateMealReviewRequest(5, null, "변경"));

        verify(reviewTranslationRepository).deleteAllByReview_Id(2L);
    }

    @Test
    void updateReviewKeepsTranslationsWhenContentUnchanged() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().id(2L).user(author).content("동일").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        reviewServiceV2.updateReview(userDetails(), 2L, new UpdateMealReviewRequest(5, null, "동일"));

        verify(reviewTranslationRepository, never()).deleteAllByReview_Id(any());
    }

    @Test
    void deleteReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.deleteReview(userDetails(), 2L)).isInstanceOf(BaseException.class);
    }

    @Test
    void deleteReviewThrowsWhenReviewDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.of(mock(User.class)));
        given(reviewRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.deleteReview(userDetails(), 2L)).isInstanceOf(BaseException.class);
    }

    @Test
    void deleteReviewRejectsNonAuthor() {
        User requester = mock(User.class);
        User author = mock(User.class);
        Review review = Review.builder().user(author).content("굿").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(requester));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        assertThatThrownBy(() -> reviewServiceV2.deleteReview(userDetails(), 2L)).isInstanceOf(BaseException.class);
    }

    @Test
    void deleteReviewRemovesReviewWrittenByAuthor() {
        User author = mock(User.class);
        given(author.getId()).willReturn(1L);
        Review review = Review.builder().user(author).content("굿").build();
        given(userRepository.findById(1L)).willReturn(Optional.of(author));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));

        reviewServiceV2.deleteReview(userDetails(), 2L);

        verify(reviewRepository).delete(review);
    }

    @Test
    void findMyReviewsReturnsSliceOfMyReviews() {
        User user = mock(User.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        Slice<Review> slice = new SliceImpl<>(List.of(), PageRequest.of(0, 20), false);
        given(reviewRepository.findByUserOrderByIdDesc(user, null, PageRequest.of(0, 20))).willReturn(slice);

        SliceResponse<?> response = reviewServiceV2.findMyReviews(userDetails(), null, PageRequest.of(0, 20));

        assertThat(response.getDataList()).isEmpty();
    }

    @Test
    void validMenuForReviewThrowsWhenMealDoesNotExist() {
        given(mealRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reviewServiceV2.validMenuForReview(1L)).isInstanceOf(BaseException.class);
    }

    @Test
    void validMenuForReviewReturnsMenusExcludingFilteredNames() {
        Meal meal = new Meal(new java.util.Date(), ssu.eatssu.domain.menu.entity.constants.TimePart.LUNCH, Restaurant.DODAM);
        given(mealRepository.findById(1L)).willReturn(Optional.of(meal));
        Menu validMenu = Menu.createVariable("돈까스", Restaurant.DODAM);
        Menu excludedMenu = Menu.createVariable("쌀밥", Restaurant.DODAM);
        given(mealMenuRepository.findMenusByMeal(meal)).willReturn(List.of(validMenu, excludedMenu));

        ValidMenuForViewResponse response = reviewServiceV2.validMenuForReview(1L);

        assertThat(response.getMenuList()).extracting("name").containsExactly("돈까스");
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
