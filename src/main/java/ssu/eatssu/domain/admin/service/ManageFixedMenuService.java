package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.admin.dto.*;
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
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ManageFixedMenuService {

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
        List<MenuCategory> menuCategories = loadMenuRepository
            .findMenuCategoriesByRestaurant(
	restaurant);

        return menuCategories.stream()
            .map(this::getMenuSection)
            .toList();
    }

    private MenuSection getMenuSection(MenuCategory category) {
        MenuSection menuSection = new MenuSection(category);

        List<BriefMenu> briefMenus = loadMenuRepository.findBriefMenusByCategoryId(
            category.getId());

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
        if (loadMenuRepository.existsMenu(request.name(), restaurant)) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }

        // 해당 식당의 메뉴 카테고리들 중에 요청받은 카테고리가 존재하는지 확인
        List<MenuCategory> menuCategories = loadMenuRepository.findMenuCategoriesByRestaurant(
            restaurant);

        MenuCategory category = menuCategories.stream()
            .filter(menuCategory -> menuCategory.getId().equals(request.categoryId()))
            .findFirst()
            .orElseThrow(() -> new BaseException(BaseResponseStatus.BAD_REQUEST));

        manageMenuRepository.save(
            Menu.createFixed(request.name(), restaurant, request.price(), category));
    }

    @Transactional
    public void updateMenu(Long menuId, UpdateFixMenuRequest request) {
        if (verifyFixedMenu(menuId)) {
            throw new BaseException(BaseResponseStatus.NOT_SUPPORT_RESTAURANT);
        }

        Menu menu = manageMenuRepository.findById(menuId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));

        // 식당에 같은 이름의 메뉴가 있는 지 확인
        Restaurant restaurant = loadMenuRepository.getRestaurant(menuId);
        verifyMenuExists(request.name(), restaurant);
        menu.update(request.name(), request.price());
    }

    private void verifyMenuExists(final String menuName, final Restaurant restaurant) {
        if (loadMenuRepository.existsMenu(menuName, restaurant)) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }
    }

    private boolean verifyFixedMenu(final Long menuId) {
        Restaurant restaurant = loadMenuRepository.getRestaurant(menuId);
        return RestaurantType.isVariableType(restaurant);
    }

    @Transactional
    public void delete(final Long menuId) {
        Menu menu = manageMenuRepository.deleteMenuById(menuId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));
        manageMenuRepository.updateSortedIndex(menu.getSortedIndex());
    }

    public Boolean changeDiscontinuedStatus(final Long menuId) {
        if (verifyFixedMenu(menuId)) {
            throw new BaseException(BaseResponseStatus.NOT_SUPPORT_RESTAURANT);
        }

        Menu menu = manageMenuRepository.findById(menuId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));

        menu.changeDiscontinuedStatus();

        return menu.isDiscontinued();
    }
}

