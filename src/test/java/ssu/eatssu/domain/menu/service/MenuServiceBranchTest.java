package ssu.eatssu.domain.menu.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MenuCategoryRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuServiceBranchTest {

    @Test
    void 기존_변동_메뉴를_재사용한다() {
        MenuRepository repository = mock(MenuRepository.class);
        Menu menu = mock(Menu.class);
        when(repository.existsByNameAndRestaurant("비빔밥", Restaurant.DODAM)).thenReturn(true);
        when(repository.findByNameAndRestaurant("비빔밥", Restaurant.DODAM)).thenReturn(Optional.of(menu));

        assertThat(service(repository).createOrGetMenu("비빔밥", Restaurant.DODAM)).isSameAs(menu);
    }

    @Test
    void 없는_변동_메뉴를_생성한다() {
        MenuRepository repository = mock(MenuRepository.class);
        Menu menu = mock(Menu.class);
        when(repository.existsByNameAndRestaurant("비빔밥", Restaurant.DODAM)).thenReturn(false);
        when(repository.save(any())).thenReturn(menu);

        assertThat(service(repository).createOrGetMenu("비빔밥", Restaurant.DODAM)).isSameAs(menu);
        verify(repository).save(any());
    }

    @Test
    void 존재한다고_판단됐지만_조회되지_않으면_예외를_반환한다() {
        MenuRepository repository = mock(MenuRepository.class);
        when(repository.existsByNameAndRestaurant("비빔밥", Restaurant.DODAM)).thenReturn(true);
        when(repository.findByNameAndRestaurant("비빔밥", Restaurant.DODAM)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service(repository).createOrGetMenu("비빔밥", Restaurant.DODAM))
                .isInstanceOf(BaseException.class);
    }

    private MenuService service(MenuRepository repository) {
        return new MenuService(repository, mock(MenuCategoryRepository.class), mock(MenuRatingService.class));
    }
}
