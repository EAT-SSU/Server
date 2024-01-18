package ssu.eatssu.domain.menu.service;

import jakarta.transaction.Transactional;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.dto.MenuRequest.CreateMealRequest;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenuInformationResponse;
import ssu.eatssu.domain.menu.dto.MenuResponse.MenusInformationResponse;
import ssu.eatssu.domain.menu.dto.MenuResponse.MealInformationResponse;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
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
    private final MealMenuRepository mealMenuRepository;

    public List<MenuInformationResponse> findMenusByRestaurant(Restaurant restaurant) {
        List<Menu> menus = menuRepository.findAllByRestaurant(restaurant);

        return menus.stream()
            .map(menu -> MenuInformationResponse.from(menu))
            .toList();
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
        CreateMealRequest request) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        if (MenuValidator.validateExistedMeal(meals,
            request)) {
            return;
        }

        Meal newMeal = Meal.builder()
            .date(date)
            .restaurant(restaurant)
            .timePart(timePart)
            .build();
        mealRepository.save(newMeal);

        addMenu(newMeal, request);
    }

    private void addMenu(Meal meal, CreateMealRequest request) {
        Restaurant restaurant = meal.getRestaurant();

        for (String addMenuName : request.getMenuNames()) {
            checkAndCreateMenu(addMenuName, restaurant);

            Menu menu = getMenu(addMenuName, restaurant);

            MealMenu mealMenu = MealMenu.builder()
	.menu(menu)
	.meal(meal)
	.build();
            mealMenuRepository.save(mealMenu);
        }
    }

    private void checkAndCreateMenu(String menuName, Restaurant restaurant) {
        if (!menuRepository.existsByNameAndRestaurant(menuName, restaurant)) {
            menuRepository.save(Menu.createVariable(menuName, restaurant));
        }

        menuRepository.save(Menu.createVariable(menuName, restaurant));
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

        cleanupGarbageMenu(menus);
    }

    private void cleanupGarbageMenu(List<Menu> menuList) {
        for (Menu menu : menuList) {
            if (menu.getMealMenus().isEmpty()) {
	menuRepository.delete(menu);
            }
        }
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
}
