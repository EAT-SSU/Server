package ssu.eatssu.domain.menu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.persistence.MenuCategoryRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.menu.presentation.dto.response.CategoryWithMenusResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenuResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenuRestaurantResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

	private final MenuRepository menuRepository;
	private final MenuCategoryRepository menuCategoryRepository;
	private final MenuRatingService menuRatingService;

	private static void validateMenuRestaurant(Restaurant restaurant) {
		if (RestaurantType.isVariableType(restaurant)) {
			throw new BaseException(NOT_SUPPORT_RESTAURANT);
		}
	}

	public MenuRestaurantResponse getMenusByRestaurant(Restaurant restaurant) {
		validateMenuRestaurant(restaurant);

		List<MenuCategory> categories = findCategoriesByRestaurant(restaurant);

		MenuRestaurantResponse response = MenuRestaurantResponse.init();

		for (MenuCategory category : categories) {
			List<MenuResponse> menus = getMenuResponsesByRestaurantAndCategory(restaurant,
				category);
			response.add(CategoryWithMenusResponse.of(category.getName(), menus));
		}

		return response;
	}

	private List<MenuCategory> findCategoriesByRestaurant(Restaurant restaurant) {
		return menuCategoryRepository.findAllByRestaurant(restaurant);
	}

	@NotNull
	private List<MenuResponse> getMenuResponsesByRestaurantAndCategory(Restaurant restaurant,
		MenuCategory category) {
		return menuRepository.findAllByRestaurantAndCategory(restaurant, category)
							 .stream()
							 .filter(Menu::isContinued)
							 .map(menu -> MenuResponse.from(menu,
								 menuRatingService.getMainRatingAverage(menu.getId())))
							 .toList();
	}

	@Transactional
	public Menu createOrGetMenu(String menuName, Restaurant restaurant) {
		return menuRepository.existsByNameAndRestaurant(menuName, restaurant) ?
			findMenu(menuName, restaurant) :
			menuRepository.save(Menu.createVariable(menuName, restaurant));
	}

	private Menu findMenu(String addMenuName, Restaurant restaurant) {
		Menu menu = menuRepository.findByNameAndRestaurant(addMenuName, restaurant)
								  .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));
		return menu;
	}

	@Transactional
	public void deleteMenu(Menu menu) {
		menuRepository.delete(menu);
	}
}