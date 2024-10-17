package ssu.eatssu.domain.menu.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.presentation.dto.CategoryWithMenusResponse;
import ssu.eatssu.domain.menu.presentation.dto.MenuRestaurantResponse;
import ssu.eatssu.domain.menu.presentation.dto.MenuResponse;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInformationResponse;
import ssu.eatssu.domain.menu.entity.*;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.persistence.MenuCategoryRepository;
import ssu.eatssu.domain.menu.persistence.MenuRepository;
import ssu.eatssu.domain.menu.util.MenuValidator;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MenuService {

    private final MenuRepository menuRepository;
    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final MenuCategoryRepository menuCategoryRepository;

    private final MealRatingService mealRatingService;
    private final MenuRatingService menuRatingService;

    public MenuRestaurantResponse findMenusByRestaurant(Restaurant restaurant) {
        validateMenuRestaurant(restaurant);

        List<MenuCategory> categories = findCategoriesByRestaurant(restaurant);

        MenuRestaurantResponse response = MenuRestaurantResponse.init();

        for (MenuCategory category : categories) {
            List<MenuResponse> menus = getMenuResponsesByRestaurantAndCategory(restaurant, category);
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

    private static void validateMenuRestaurant(Restaurant restaurant) {
        if (RestaurantType.isVariableType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }

    public List<MealDetailResponse> findSpecificMeals(Date date,
        Restaurant restaurant,
        TimePart timePart) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        return meals.stream()
            .map(meal -> MealDetailResponse.from(meal,
	mealRatingService.getMainRatingAverage(meal.getId())))
            .toList();
    }


    // TODO 삭제할까 말까
    public void createMeal(Date date, Restaurant restaurant, TimePart timePart,
        MealCreateRequest request) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        if (MenuValidator.validateExistedMeal(meals, request.menuNames())) {
            return;
        }
        Meal meal = new Meal(date, timePart, restaurant);

        mealRepository.save(meal);

        addMenusToMeal(meal, restaurant, request.menuNames());
    }

    /**
     * 가격 추가 용
     */
    public void createMealWithPrice(Date date, Restaurant restaurant, TimePart timePart,
        MealCreateWithPriceRequest request) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        if (MenuValidator.validateExistedMeal(meals, request.menuNames())) {
            return;
        }
        Meal meal = new Meal(date, timePart, restaurant, request.price());

        mealRepository.save(meal);

        addMenusToMeal(meal, restaurant, request.menuNames());
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