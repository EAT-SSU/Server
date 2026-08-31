package ssu.eatssu.domain.review.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.context.ApplicationEventPublisher;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.menu.service.MealRatingService;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.review.repository.ReviewTranslationRepository;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReviewServiceV2Test {

    @Test
    void 존재하지_않는_식단의_리뷰는_조회하지_않는다() {
        MealRepository mealRepository = mock(MealRepository.class);
        when(mealRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(mealRepository, mock(MealMenuRepository.class))
                .findMealReviewList(1L, null, PageRequest.of(0, 20), null))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void 리뷰_가능한_메뉴가_없으면_빈_목록을_반환한다() {
        MealRepository mealRepository = mock(MealRepository.class);
        MealMenuRepository mealMenuRepository = mock(MealMenuRepository.class);
        Meal meal = mock(Meal.class);
        when(mealRepository.findById(1L)).thenReturn(Optional.of(meal));
        when(mealMenuRepository.findMenusByMeal(meal)).thenReturn(List.of());

        var response = service(mealRepository, mealMenuRepository)
                .findMealReviewList(1L, null, PageRequest.of(0, 20), null);

        assertThat(response.getDataList()).isEmpty();
        assertThat(response.isHasNext()).isFalse();
    }

    private ReviewServiceV2 service(MealRepository mealRepository, MealMenuRepository mealMenuRepository) {
        return new ReviewServiceV2(mock(UserRepository.class), mock(ReviewRepository.class),
                                   mock(ReviewTranslationRepository.class), mock(MenuRepository.class), mealRepository,
                                   mealMenuRepository, mock(ApplicationEventPublisher.class), mock(MealRatingService.class));
    }
}
