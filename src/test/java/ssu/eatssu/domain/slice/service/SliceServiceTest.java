package ssu.eatssu.domain.slice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.MenuType;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SliceServiceTest {

    private static final PageRequest PAGEABLE = PageRequest.of(0, 10);

    @Mock private UserRepository userRepository;
    @Mock private ReviewRepository reviewRepository;
    @Mock private MenuRepository menuRepository;
    @Mock private MealRepository mealRepository;

    @InjectMocks private SliceService sliceService;

    @Test
    void findReviewsReturnsEmptySliceForFixedMenu() {
        Menu menu = org.mockito.Mockito.mock(Menu.class);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.findAllByMenuOrderByIdDesc(menu, null, PAGEABLE))
                .willReturn(new SliceImpl<>(List.of(), PAGEABLE, false));

        SliceResponse<?> response = sliceService.findReviews(MenuType.FIXED, 1L, null, PAGEABLE, null, null);

        assertThat(response.getNumberOfElements()).isZero();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getDataList()).isEmpty();
    }

    @Test
    void findReviewsThrowsWhenFixedMenuDoesNotExist() {
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sliceService.findReviews(MenuType.FIXED, 1L, null, PAGEABLE, null, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void findReviewsThrowsWhenVariableMealDoesNotExist() {
        given(mealRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sliceService.findReviews(MenuType.VARIABLE, null, 2L, PAGEABLE, null, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void findReviewsReturnsSliceForVariableMealWithLoggedInUser() {
        Meal meal = org.mockito.Mockito.mock(Meal.class);
        given(mealRepository.findById(2L)).willReturn(Optional.of(meal));
        given(reviewRepository.findAllByMealOrderByIdDesc(meal, null, PAGEABLE))
                .willReturn(new SliceImpl<>(List.of(), PAGEABLE, false));

        SliceResponse<?> response = sliceService.findReviews(MenuType.VARIABLE, null, 2L, PAGEABLE, null, userDetails());

        assertThat(response.getDataList()).isEmpty();
    }

    @Test
    void findReviewsV1ReturnsSliceForFixedMenu() {
        Menu menu = org.mockito.Mockito.mock(Menu.class);
        given(menuRepository.findById(1L)).willReturn(Optional.of(menu));
        given(reviewRepository.findAllByMenuOrderByIdDesc(menu, null, PAGEABLE))
                .willReturn(new SliceImpl<>(List.of(), PAGEABLE, false));

        SliceResponse<?> response = sliceService.findReviewsV1(MenuType.FIXED, 1L, null, PAGEABLE, null, null);

        assertThat(response.getDataList()).isEmpty();
    }

    @Test
    void findReviewsV1ThrowsWhenFixedMenuDoesNotExist() {
        given(menuRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sliceService.findReviewsV1(MenuType.FIXED, 1L, null, PAGEABLE, null, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void findReviewsV1ReturnsSliceForVariableMealWithLoggedInUser() {
        Meal meal = org.mockito.Mockito.mock(Meal.class);
        given(mealRepository.findById(2L)).willReturn(Optional.of(meal));
        given(reviewRepository.findAllByMealOrderByIdDesc(meal, null, PAGEABLE))
                .willReturn(new SliceImpl<>(List.of(), PAGEABLE, false));

        SliceResponse<?> response = sliceService.findReviewsV1(MenuType.VARIABLE, null, 2L, PAGEABLE, null,
                                                                userDetails());

        assertThat(response.getDataList()).isEmpty();
    }

    @Test
    void findReviewsV1ThrowsWhenVariableMealDoesNotExist() {
        given(mealRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sliceService.findReviewsV1(MenuType.VARIABLE, null, 2L, PAGEABLE, null, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void findMyReviewsThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> sliceService.findMyReviews(userDetails(), PAGEABLE, null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void findMyReviewsFiltersNullReviewEntries() {
        User user = org.mockito.Mockito.mock(User.class);
        @SuppressWarnings("unchecked")
        Slice<Review> reviews = org.mockito.Mockito.mock(Slice.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reviewRepository.findByUserOrderByIdDesc(user, null, PAGEABLE)).willReturn(reviews);
        given(reviews.getContent()).willReturn(Arrays.asList((Review) null));
        given(reviews.getNumberOfElements()).willReturn(1);
        given(reviews.hasNext()).willReturn(true);

        SliceResponse<?> response = sliceService.findMyReviews(userDetails(), PAGEABLE, null);

        assertThat(response.getNumberOfElements()).isEqualTo(1);
        assertThat(response.isHasNext()).isTrue();
        assertThat(response.getDataList()).isEmpty();
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
