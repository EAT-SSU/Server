package ssu.eatssu.domain.menu.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.dto.*;
import ssu.eatssu.domain.menu.entity.*;
import ssu.eatssu.domain.menu.repository.MealMenuRepository;
import ssu.eatssu.domain.menu.repository.MealRepository;
import ssu.eatssu.domain.menu.repository.MenuCategoryRepository;
import ssu.eatssu.domain.menu.repository.MenuRepository;
import ssu.eatssu.domain.menu.util.MenuValidator;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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

    public CategoryMenuListCollection findMenusByRestaurant(Restaurant restaurant) {
        List<MenuCategory> categories = menuCategoryRepository.findAllByRestaurant(restaurant);
        CategoryMenuListCollection collection = CategoryMenuListCollection.init();
        for (MenuCategory category : categories) {
            List<MenuInformation> menus = menuRepository.findAllByRestaurantAndCategory(
                            restaurant, category).stream()
                    .filter(menu -> !menu.isDiscontinued())
                    .map(menu -> MenuInformation.from(menu, menuRatingService.getMainRatingAverage(menu.getId())))
                    .toList();
            CategoryMenuList categoryMenuList = new CategoryMenuList(category.getName(), menus);
            collection.add(categoryMenuList);
        }

        return collection;
    }

    public List<MealInformationResponse> findSpecificMeals(Date date,
                                                           Restaurant restaurant,
                                                           TimePart timePart) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        return meals.stream()
                .map(meal -> MealInformationResponse.from(meal, mealRatingService.getMainRatingAverage(meal.getId())))
                .toList();
    }


    // TODO 삭제할까 말까
    public void createMeal(Date date, Restaurant restaurant, TimePart timePart,
                           MealCreateRequest request) {
        List<Meal> meals = getMeals(date, timePart, restaurant);

        if (MenuValidator.validateExistedMeal(meals, request)) {
            return;
        }
        Meal meal = new Meal(date, timePart, restaurant);

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