package ssu.eatssu.domain.menu.service;

import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.dto.MenuRequest.MealCreateRequest;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenuInformationResponse;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenusInformationResponse;
import ssu.eatssu.domain.menu.dto.MenuResponse.MealInformationResponse;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.MenuCategory;
import ssu.eatssu.domain.menu.repository.MenuCategoryRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.domain.menu.repository.MealMenuRepository;
import ssu.eatssu.domain.menu.repository.MealRepository;
import ssu.eatssu.domain.menu.repository.MenuRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.util.List;
import ssu.eatssu.global.handler.response.BaseResponseStatus;
import ssu.eatssu.domain.menu.util.MenuValidator;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MenuCategoryRepository menuCategoryRepository;
    private final MealMenuRepository mealMenuRepository;

    public Map<String, List<MenuInformationResponse>> findMenusByRestaurant(Restaurant restaurant) {
        List<MenuCategory> categories = menuCategoryRepository.findAllByRestaurant(restaurant);
        Map<String, List<MenuInformationResponse>> categoryMenus = new HashMap<>();

        for (MenuCategory category : categories) {

            List<MenuInformationResponse> menus = menuRepository.findAllByRestaurantAndCategory(
	    restaurant, category).stream()
	.filter(menu -> !menu.isDiscontinued())
	.map(menu -> MenuInformationResponse.from(menu))
	.toList();

            categoryMenus.put(category.getName(), menus);
        }

        return categoryMenus;
    }

    public List<MealInformationResponse> findSpecificMeals(Date date,
        Restaurant restaurant,
        TimePart timePart) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        return meals.stream()
            .map(meal -> MealInformationResponse.from(meal))
            .toList();
    }

    public void createMeal(Date date, Restaurant restaurant, TimePart timePart,
        MealCreateRequest request) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        if (MenuValidator.validateExistedMeal(meals, request)) {
            return;
        }
        Meal meal = new Meal(date, timePart, restaurant);

        mealRepository.save(meal);

        addMenusToMeal(meal, restaurant, request.getMenuNames());
    }

    public MenusInformationResponse findMenusInMeal(Long mealId) {
        Meal meal = getMeal(mealId);
        return MenusInformationResponse.from(meal);
    }

    public void deleteMeal(Long mealId) {
        Meal meal = getMeal(mealId);

        List<Menu> menus = meal.getMealMenus().stream()
            .map(MealMenu::getMenu)
            .toList();

        mealRepository.delete(meal);
        mealRepository.flush();

        deleteUnusedMenus(menus);
    }

    private void addMenusToMeal(Meal meal, Restaurant restaurant, List<String> menuNames) {
        for (String menuName : menuNames) {
            Menu menu = createMenuIfNotExists(menuName, restaurant);
            createMealMenu(meal, menu);
        }
    }

    private Menu createMenuIfNotExists(String menuName, Restaurant restaurant) {
        return menuRepository.existsByNameAndRestaurant(menuName, restaurant) ?
            getMenu(menuName, restaurant) :
            menuRepository.save(Menu.createVariable(menuName, restaurant));
    }

    private void createMealMenu(Meal meal, Menu menu) {
        MealMenu mealMenu = MealMenu.builder()
            .menu(menu)
            .meal(meal)
            .build();
        mealMenuRepository.save(mealMenu);
    }

    private Menu getMenu(String addMenuName, Restaurant restaurant) {
        Menu menu = menuRepository.findByNameAndRestaurant(addMenuName, restaurant)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));
        return menu;
    }

    private Meal getMeal(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
            .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));
        return meal;
    }

    private List<Meal> getMeals(Date date, TimePart timePart, Restaurant restaurant) {
        List<Meal> meals = mealRepository.findAllByDateAndTimePartAndRestaurant(date,
            timePart, restaurant);
        return meals;
    }

    private void deleteUnusedMenus(List<Menu> menus) {
        menus.stream()
            .filter(menu -> menu.getMealMenus().isEmpty())
            .forEach(menuRepository::delete);
    }
}