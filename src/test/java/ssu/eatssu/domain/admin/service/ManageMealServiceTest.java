package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.dto.MealInfo;
import ssu.eatssu.domain.admin.dto.request.RegisterMealRequest;
import ssu.eatssu.domain.admin.persistence.LoadMealRepository;
import ssu.eatssu.domain.admin.persistence.ManageMealMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMealRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManageMealServiceTest {

    private static final MealInfo MEAL_INFO = new MealInfo(Restaurant.DODAM, new Date(), TimePart.LUNCH);

    @Test
    void 식단을_삭제하고_더_사용되지_않는_메뉴만_삭제한다() {
        LoadMealRepository loader = mock(LoadMealRepository.class);
        ManageMealRepository mealRepository = mock(ManageMealRepository.class);
        ManageMenuRepository menuRepository = mock(ManageMenuRepository.class);
        when(loader.getAllMenuIds(10L)).thenReturn(List.of(1L, 2L));
        when(loader.countMealMenuByMenuId(1L)).thenReturn(0);
        when(loader.countMealMenuByMenuId(2L)).thenReturn(1);
        ManageMealService service = new ManageMealService(loader, mock(MenuRatingRepository.class), mealRepository,
                                                           mock(ManageMealMenuRepository.class), menuRepository);

        service.delete(10L);

        verify(mealRepository).deleteById(10L);
        verify(menuRepository).deleteById(1L);
    }

    @Test
    void registerCreatesNewMenuWhenMenuNameDoesNotExistYet() {
        LoadMealRepository loader = mock(LoadMealRepository.class);
        ManageMenuRepository menuRepository = mock(ManageMenuRepository.class);
        ManageMealMenuRepository mealMenuRepository = mock(ManageMealMenuRepository.class);
        when(loader.existsMeal(MEAL_INFO)).thenReturn(List.of());
        when(loader.findMenu("돈까스", Restaurant.DODAM)).thenReturn(null);
        ManageMealService service = new ManageMealService(loader, mock(MenuRatingRepository.class),
                                                           mock(ManageMealRepository.class), mealMenuRepository,
                                                           menuRepository);

        service.register(MEAL_INFO, new RegisterMealRequest(new ArrayList<>(List.of("돈까스"))));

        verify(menuRepository).save(any(Menu.class));
        verify(mealMenuRepository).save(any(MealMenu.class));
    }

    @Test
    void registerReusesExistingMenuWhenMenuNameAlreadyExists() {
        LoadMealRepository loader = mock(LoadMealRepository.class);
        ManageMenuRepository menuRepository = mock(ManageMenuRepository.class);
        ManageMealMenuRepository mealMenuRepository = mock(ManageMealMenuRepository.class);
        Menu existingMenu = Menu.createVariable("돈까스", Restaurant.DODAM);
        when(loader.existsMeal(MEAL_INFO)).thenReturn(List.of());
        when(loader.findMenu("돈까스", Restaurant.DODAM)).thenReturn(existingMenu);
        ManageMealService service = new ManageMealService(loader, mock(MenuRatingRepository.class),
                                                           mock(ManageMealRepository.class), mealMenuRepository,
                                                           menuRepository);

        service.register(MEAL_INFO, new RegisterMealRequest(new ArrayList<>(List.of("돈까스"))));

        verify(menuRepository, never()).save(any(Menu.class));
        verify(mealMenuRepository).save(any(MealMenu.class));
    }

    @Test
    void registerThrowsWhenMealWithSameMenusAlreadyExists() {
        LoadMealRepository loader = mock(LoadMealRepository.class);
        when(loader.existsMeal(MEAL_INFO)).thenReturn(List.of(5L));
        when(loader.findMenuNamesByMealId(5L)).thenReturn(List.of("돈까스"));
        ManageMealService service = new ManageMealService(loader, mock(MenuRatingRepository.class),
                                                           mock(ManageMealRepository.class),
                                                           mock(ManageMealMenuRepository.class),
                                                           mock(ManageMenuRepository.class));

        assertThatThrownBy(() -> service.register(MEAL_INFO, new RegisterMealRequest(new ArrayList<>(List.of("돈까스")))))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void registerDoesNotThrowWhenExistingMealHasDifferentMenus() {
        LoadMealRepository loader = mock(LoadMealRepository.class);
        ManageMealMenuRepository mealMenuRepository = mock(ManageMealMenuRepository.class);
        when(loader.existsMeal(MEAL_INFO)).thenReturn(List.of(5L));
        when(loader.findMenuNamesByMealId(5L)).thenReturn(List.of("김치찌개"));
        when(loader.findMenu("돈까스", Restaurant.DODAM)).thenReturn(Menu.createVariable("돈까스", Restaurant.DODAM));
        ManageMealService service = new ManageMealService(loader, mock(MenuRatingRepository.class),
                                                           mock(ManageMealRepository.class), mealMenuRepository,
                                                           mock(ManageMenuRepository.class));

        service.register(MEAL_INFO, new RegisterMealRequest(new ArrayList<>(List.of("돈까스"))));

        verify(mealMenuRepository).save(any(MealMenu.class));
    }
}
