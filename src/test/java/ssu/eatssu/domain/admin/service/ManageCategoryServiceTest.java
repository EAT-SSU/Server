package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.dto.request.RegisterCategoryRequest;
import ssu.eatssu.domain.admin.persistence.LoadCategoryRepository;
import ssu.eatssu.domain.admin.persistence.ManageCategoryRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ManageCategoryServiceTest {

    @Test
    void 새_카테고리를_저장한다() {
        LoadCategoryRepository loader = mock(LoadCategoryRepository.class);
        ManageCategoryRepository repository = mock(ManageCategoryRepository.class);
        when(loader.existsCategory(Restaurant.FOOD_COURT, "한식")).thenReturn(false);

        new ManageCategoryService(loader, repository).register(Restaurant.FOOD_COURT, new RegisterCategoryRequest("한식"));

        verify(repository).save(any());
    }

    @Test
    void 중복된_카테고리는_저장하지_않는다() {
        LoadCategoryRepository loader = mock(LoadCategoryRepository.class);
        ManageCategoryRepository repository = mock(ManageCategoryRepository.class);
        when(loader.existsCategory(Restaurant.FOOD_COURT, "한식")).thenReturn(true);

        assertThatThrownBy(() -> new ManageCategoryService(loader, repository)
                .register(Restaurant.FOOD_COURT, new RegisterCategoryRequest("한식")))
                .isInstanceOf(BaseException.class);
        verify(repository, never()).save(any());
    }
}
