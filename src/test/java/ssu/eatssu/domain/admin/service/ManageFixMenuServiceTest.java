package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ssu.eatssu.domain.admin.dto.request.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.persistence.LoadFixMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ManageFixMenuServiceTest {

    @Test
    void registerSavesMenuForMatchingCategory() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);
        MenuCategory category = MenuCategory.builder().id(1L).restaurant(Restaurant.FOOD_COURT).build();
        given(loadRepository.existsMenu("돈가스", Restaurant.FOOD_COURT)).willReturn(false);
        given(loadRepository.findMenuCategoriesByRestaurant(Restaurant.FOOD_COURT)).willReturn(List.of(category));
        ManageFixMenuService service = service(loadRepository, manageRepository);

        service.register(Restaurant.FOOD_COURT, new RegisterFixMenuRequest(1L, "돈가스", 6000));

        ArgumentCaptor<Menu> menuCaptor = ArgumentCaptor.forClass(Menu.class);
        verify(manageRepository).save(menuCaptor.capture());
        assertThat(menuCaptor.getValue().getCategory()).isSameAs(category);
    }

    @Test
    void registerRejectsDuplicateOrMissingCategory() {
        LoadFixMenuRepository duplicateRepository = mock(LoadFixMenuRepository.class);
        given(duplicateRepository.existsMenu("돈가스", Restaurant.FOOD_COURT)).willReturn(true);

        assertThatThrownBy(() -> service(duplicateRepository, mock(ManageMenuRepository.class))
                .register(Restaurant.FOOD_COURT, new RegisterFixMenuRequest(1L, "돈가스", 6000)))
                .isInstanceOf(BaseException.class);

        LoadFixMenuRepository categoryRepository = mock(LoadFixMenuRepository.class);
        given(categoryRepository.existsMenu("돈가스", Restaurant.FOOD_COURT)).willReturn(false);
        given(categoryRepository.findMenuCategoriesByRestaurant(Restaurant.FOOD_COURT)).willReturn(List.of());

        assertThatThrownBy(() -> service(categoryRepository, mock(ManageMenuRepository.class))
                .register(Restaurant.FOOD_COURT, new RegisterFixMenuRequest(1L, "돈가스", 6000)))
                .isInstanceOf(BaseException.class);
    }

    private ManageFixMenuService service(LoadFixMenuRepository loadRepository, ManageMenuRepository manageRepository) {
        return new ManageFixMenuService(loadRepository, manageRepository, mock(MenuRatingRepository.class));
    }
}
