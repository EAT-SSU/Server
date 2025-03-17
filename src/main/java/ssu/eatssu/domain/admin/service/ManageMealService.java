package ssu.eatssu.domain.admin.service;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.admin.dto.BriefMenu;
import ssu.eatssu.domain.admin.dto.MealInfo;
import ssu.eatssu.domain.admin.dto.MealSection;
import ssu.eatssu.domain.admin.dto.MenuBoard;
import ssu.eatssu.domain.admin.dto.MenuBoards;
import ssu.eatssu.domain.admin.dto.MenuLine;
import ssu.eatssu.domain.admin.dto.RegisterMealRequest;
import ssu.eatssu.domain.admin.persistence.LoadMealRepository;
import ssu.eatssu.domain.admin.persistence.ManageMealMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMealRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@RequiredArgsConstructor
@Service
public class ManageMealService {

	private final LoadMealRepository loadMealRepository;
	private final MenuRatingRepository menuRatingRepository;
	private final ManageMealRepository manageMealRepository;
	private final ManageMealMenuRepository manageMealMenuRepository;
	private final ManageMenuRepository manageMenuRepository;

	public MenuBoards getMenuBoards(Date date, TimePart timePart) {
		MenuBoards menuBoards = new MenuBoards();

		RestaurantType.VARIABLE.getRestaurants()
							   .forEach(restaurant -> menuBoards.add(
								   getMealBoard(new MealInfo(restaurant, date, timePart))));

		return menuBoards;
	}

	private MenuBoard getMealBoard(MealInfo mealInfo) {
		MenuBoard menuBoard = new MenuBoard(mealInfo.restaurant().getRestaurantName());

		getMenuSections(mealInfo).forEach(menuBoard::addSection);

		return menuBoard;
	}

	private List<MealSection> getMenuSections(MealInfo mealInfo) {
		List<Long> mealIds = loadMealRepository.getAllMealIdsByInfo(mealInfo);

		return mealIds.stream().map(this::getMenuSection).toList();
	}

	private MealSection getMenuSection(Long mealId) {
		MealSection mealSection = new MealSection(mealId);

		List<BriefMenu> briefMenus = loadMealRepository.findBriefMenusByMealId(mealId);

		briefMenus.forEach(briefMenu ->
			mealSection.addMenuLine(new MenuLine(
				briefMenu.id(),
				briefMenu.name(),
				briefMenu.price(),
				menuRatingRepository.getMainRatingAverage(briefMenu.id())
			))
		);

		return mealSection;
	}

	public void register(MealInfo mealInfo, RegisterMealRequest request) {
		// 중복 체크
		duplicateCheck(mealInfo, request);

		// 식단 등록
		Meal meal = new Meal(mealInfo.date(), mealInfo.timePart(), mealInfo.restaurant());
		manageMealRepository.save(meal);

		//식단에 메뉴 추가
		addMenusToMeal(meal, request.menuNames());

	}

	private void addMenusToMeal(Meal meal, List<String> menuNames) {
		menuNames.forEach(menuName -> {
			Menu menu = loadMealRepository.findMenu(menuName, meal.getRestaurant());
			if (menu == null) {
				menu = Menu.createVariable(menuName, meal.getRestaurant());
				manageMenuRepository.save(menu);
			}
			manageMealMenuRepository.save(MealMenu.builder().meal(meal).menu(menu).build());
		});
	}

	private void duplicateCheck(MealInfo mealInfo, RegisterMealRequest request) {
		List<Long> mealIds = loadMealRepository.existsMeal(mealInfo);
		mealIds.forEach(mealId -> {
			if (hasMenus(mealId, request.menuNames())) {
				throw new BaseException(BaseResponseStatus.CONFLICT);
			}
		});
	}

	private boolean hasMenus(Long mealId, List<String> menuNames) {
		List<String> menuNamesInMeal = loadMealRepository.findMenuNamesByMealId(mealId);

		/**
		 * menuNamesInMeal 과 menuNames 는 오름차순 정렬된 상태
		 * @see LoadMealRepository#findMenuNamesByMealId(Long)
		 * @see RegisterMealRequest#RegisterMealRequest
		 */
		return menuNamesInMeal.equals(menuNames);
	}

	public void delete(Long mealId) {
		List<Long> menuIds = loadMealRepository.getAllMenuIds(mealId);
		manageMealRepository.deleteById(mealId);
		deleteUnusedMenus(menuIds);
	}

	private void deleteUnusedMenus(List<Long> menuIds) {
		menuIds.forEach(menuId -> {
			if (loadMealRepository.countMealMenuByMenuId(menuId) == 0) {
				manageMenuRepository.deleteById(menuId);
			}
		});
	}
}
