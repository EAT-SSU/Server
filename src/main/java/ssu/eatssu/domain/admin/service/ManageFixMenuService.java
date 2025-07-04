package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.admin.dto.MenuBoard;
import ssu.eatssu.domain.admin.dto.MenuBoards;
import ssu.eatssu.domain.admin.dto.MenuLine;
import ssu.eatssu.domain.admin.dto.MenuSection;
import ssu.eatssu.domain.admin.dto.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.dto.UpdateFixMenuRequest;
import ssu.eatssu.domain.admin.persistence.LoadFixMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ManageFixMenuService {

    private final LoadFixMenuRepository loadMenuRepository;
    private final ManageMenuRepository manageMenuRepository;
    private final MenuRatingRepository menuRatingRepository;

    public MenuBoards getMenuBoards() {
        MenuBoards menuBoards = new MenuBoards();

        RestaurantType.FIXED.getRestaurants()
                            .forEach(restaurant -> menuBoards.add(getMenuBoard(restaurant)));

        return menuBoards;
    }

    private MenuBoard getMenuBoard(Restaurant restaurant) {
        MenuBoard menuBoard = new MenuBoard(restaurant.getRestaurantName());

        getMenuSections(restaurant).forEach(menuBoard::addSection);

        return menuBoard;
    }

    private List<MenuSection> getMenuSections(Restaurant restaurant) {
        List<MenuCategory> menuCategories = loadMenuRepository.findMenuCategoriesByRestaurant(restaurant);
        return menuCategories.stream()
                             .map(this::getMenuSection)
                             .toList();
    }

    private MenuSection getMenuSection(MenuCategory category) {
        MenuSection menuSection = new MenuSection(category);

        List<BriefMenu> briefMenus = loadMenuRepository.findBriefMenusByCategoryId(category.getId());

        briefMenus.forEach(briefMenu ->
                                   menuSection.addMenuLine(new MenuLine(
                                           briefMenu.id(),
                                           briefMenu.name(),
                                           briefMenu.price(),
                                           menuRatingRepository.getMainRatingAverage(briefMenu.id())
                                   ))
                          );
        return menuSection;
    }

    public void register(Restaurant restaurant, RegisterFixMenuRequest request) {

        // 해당 식당에 이미 존재하는 메뉴인지 확인
        if (loadMenuRepository.existsMenu(request.name(), restaurant)) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }

        // 해당 식당의 메뉴 카테고리들 중에 요청받은 카테고리가 존재하는지 확인
        List<MenuCategory> menuCategories = loadMenuRepository.findMenuCategoriesByRestaurant(restaurant);
        MenuCategory category = menuCategories.stream()
                                              .filter(menuCategory -> menuCategory.getId().equals(request.categoryId()))
                                              .findFirst()
                                              .orElseThrow(() -> new BaseException(BaseResponseStatus.BAD_REQUEST));

        manageMenuRepository.save(Menu.createFixed(request.name(), restaurant, request.price(), category));
    }

    public void updateMenu(Long menuId, UpdateFixMenuRequest request) {
        if (isFixedMenu(menuId)) {
            throw new BaseException(BaseResponseStatus.NOT_SUPPORT_RESTAURANT);
        }

        Menu menu = manageMenuRepository.findById(menuId)
                                        .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));

        // 식당에 같은 이름의 메뉴가 있는 지 확인
        Restaurant restaurant = loadMenuRepository.getRestaurant(menuId);
        duplicateCheck(request.name(), restaurant);
        menu.update(request.name(), request.price());

        manageMenuRepository.save(menu);
    }

    private void duplicateCheck(String name, Restaurant restaurant) {
        if (loadMenuRepository.existsMenu(name, restaurant)) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }
    }

    private boolean isFixedMenu(Long menuId) {
        Restaurant restaurant = loadMenuRepository.getRestaurant(menuId);
        return RestaurantType.isVariableType(restaurant);
    }

    public void delete(Long menuId) {
        manageMenuRepository.deleteById(menuId);
    }

    public Boolean changeDiscontinuedStatus(Long menuId) {
        if (isFixedMenu(menuId)) {
            throw new BaseException(BaseResponseStatus.NOT_SUPPORT_RESTAURANT);
        }

        Menu menu = manageMenuRepository.findById(menuId)
                                        .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));

        menu.changeDiscontinuedStatus();

        return menu.isDiscontinued();
    }
}

