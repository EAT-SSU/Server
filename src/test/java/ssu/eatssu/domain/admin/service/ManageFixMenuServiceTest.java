package ssu.eatssu.domain.admin.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.admin.dto.request.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.dto.request.UpdateFixMenuRequest;
import ssu.eatssu.domain.admin.dto.response.MenuBoards;
import ssu.eatssu.domain.admin.persistence.LoadFixMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void updateMenuThrowsWhenRestaurantIsVariableType() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.DODAM);

        assertThatThrownBy(() -> service(loadRepository, mock(ManageMenuRepository.class))
                .updateMenu(1L, new UpdateFixMenuRequest("돈가스", 6000)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateMenuThrowsWhenMenuDoesNotExist() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.FOOD_COURT);
        given(manageRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service(loadRepository, manageRepository)
                .updateMenu(1L, new UpdateFixMenuRequest("돈가스", 6000)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateMenuThrowsWhenNewNameAlreadyExists() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);
        Menu menu = Menu.createFixed("돈가스", Restaurant.FOOD_COURT, 5000, null);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.FOOD_COURT);
        given(manageRepository.findById(1L)).willReturn(Optional.of(menu));
        given(loadRepository.existsMenu("김치볶음밥", Restaurant.FOOD_COURT)).willReturn(true);

        assertThatThrownBy(() -> service(loadRepository, manageRepository)
                .updateMenu(1L, new UpdateFixMenuRequest("김치볶음밥", 6000)))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void updateMenuUpdatesNameAndPriceWhenValid() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);
        Menu menu = Menu.createFixed("돈가스", Restaurant.FOOD_COURT, 5000, null);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.FOOD_COURT);
        given(manageRepository.findById(1L)).willReturn(Optional.of(menu));
        given(loadRepository.existsMenu("김치볶음밥", Restaurant.FOOD_COURT)).willReturn(false);

        service(loadRepository, manageRepository).updateMenu(1L, new UpdateFixMenuRequest("김치볶음밥", 6000));

        assertThat(menu.getName()).isEqualTo("김치볶음밥");
        assertThat(menu.getPrice()).isEqualTo(6000);
        verify(manageRepository).save(menu);
    }

    @Test
    void changeDiscontinuedStatusThrowsWhenRestaurantIsVariableType() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.DODAM);

        assertThatThrownBy(() -> service(loadRepository, mock(ManageMenuRepository.class))
                .changeDiscontinuedStatus(1L))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void changeDiscontinuedStatusThrowsWhenMenuDoesNotExist() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.FOOD_COURT);
        given(manageRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service(loadRepository, manageRepository).changeDiscontinuedStatus(1L))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void changeDiscontinuedStatusTogglesAndReturnsNewStatus() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);
        Menu menu = Menu.createFixed("돈가스", Restaurant.FOOD_COURT, 5000, null);
        given(loadRepository.getRestaurant(1L)).willReturn(Restaurant.FOOD_COURT);
        given(manageRepository.findById(1L)).willReturn(Optional.of(menu));

        Boolean result = service(loadRepository, manageRepository).changeDiscontinuedStatus(1L);

        assertThat(result).isTrue();
        assertThat(menu.isDiscontinued()).isTrue();
    }

    @Test
    void deleteRemovesMenuById() {
        ManageMenuRepository manageRepository = mock(ManageMenuRepository.class);

        service(mock(LoadFixMenuRepository.class), manageRepository).delete(1L);

        verify(manageRepository).deleteById(1L);
    }

    @Test
    void getMenuBoardsBuildsBoardsWithMenuLinesForEachFixedRestaurant() {
        LoadFixMenuRepository loadRepository = mock(LoadFixMenuRepository.class);
        MenuRatingRepository ratingRepository = mock(MenuRatingRepository.class);
        MenuCategory category = MenuCategory.builder().id(1L).name("한식").restaurant(Restaurant.FOOD_COURT).build();
        given(loadRepository.findMenuCategoriesByRestaurant(any(Restaurant.class))).willReturn(List.of());
        given(loadRepository.findMenuCategoriesByRestaurant(Restaurant.FOOD_COURT)).willReturn(List.of(category));
        given(loadRepository.findBriefMenusByCategoryId(1L)).willReturn(List.of(new BriefMenu(10L, "라면", 3000)));
        given(ratingRepository.getMainRatingAverage(10L)).willReturn(4.0);
        ManageFixMenuService service = new ManageFixMenuService(loadRepository, mock(ManageMenuRepository.class),
                                                                 ratingRepository);

        MenuBoards boards = service.getMenuBoards();

        assertThat(boards.menuBoards()).hasSize(2);
        assertThat(boards.menuBoards()).anySatisfy(board -> {
            if (board.restaurantName().equals(Restaurant.FOOD_COURT.getRestaurantName())) {
                assertThat(board.sections()).hasSize(1);
            }
        });
    }

    private ManageFixMenuService service(LoadFixMenuRepository loadRepository, ManageMenuRepository manageRepository) {
        return new ManageFixMenuService(loadRepository, manageRepository, mock(MenuRatingRepository.class));
    }
}
