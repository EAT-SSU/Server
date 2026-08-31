package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.persistence.LoadMealRepository;
import ssu.eatssu.domain.admin.persistence.ManageMealMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMealRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManageMealServiceTest {

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
}
