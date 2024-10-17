package ssu.eatssu.domain.menu.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final MealRatingService mealRatingService;
    private final MenuService menuService;

    public MenusInMealResponse getMenusInMealByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));

        return MenusInMealResponse.from(meal);
    }

    public List<MealDetailResponse> getMealDetailsByDateAndRestaurantAndTimePart(
        Date date, Restaurant restaurant, TimePart timePart) {

        validateMealRestaurant(restaurant);

        List<Meal> meals = findMealsByDateAndTimePartAndRestaurant(date, timePart, restaurant);

        return meals.stream()
            .map(meal -> MealDetailResponse.from(meal, mealRatingService.getMainRatingAverage(meal.getId())))
            .toList();
    }

    private List<Meal> findMealsByDateAndTimePartAndRestaurant(Date date, TimePart timePart, Restaurant restaurant) {
        return mealRepository.findAllByDateAndTimePartAndRestaurant(date, timePart, restaurant);
    }

    private void validateMealRestaurant(Restaurant restaurant) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }

    @Transactional
    public void createMeal(Date date, Restaurant restaurant, TimePart timePart, MealCreateRequest request) {
        createMealWithOptionalPrice(date, restaurant, timePart, request.menuNames(), null);
    }

    @Transactional
    public void createMealWithPrice(Date date, Restaurant restaurant, TimePart timePart, MealCreateWithPriceRequest request) {
        createMealWithOptionalPrice(date, restaurant, timePart, request.menuNames(), request.price());
    }

    private void createMealWithOptionalPrice(Date date, Restaurant restaurant, TimePart timePart, List<String> menuNames, Integer price) {
        if (validateExistedMeal(date, timePart, restaurant, menuNames)) {
            return; // 중복된 메뉴가 있는 경우 바로 종료
        }

        Meal meal = new Meal(date, timePart, restaurant, price);
        mealRepository.save(meal);
        addMenusToMeal(meal, restaurant, menuNames);
    }

    private boolean validateExistedMeal(Date date, TimePart timePart, Restaurant restaurant, List<String> menuNames) {
        List<Meal> meals = mealRepository.findAllByDateAndTimePartAndRestaurant(date, timePart, restaurant);

        List<String> sortedRequestMenuNames = menuNames.stream()
            .sorted()
            .toList();

        return meals.stream().anyMatch(meal -> {
            List<String> sortedMenuNames = meal.getMenuNames().stream()
                .sorted()
                .toList();
            return sortedMenuNames.equals(sortedRequestMenuNames);
        });
    }

    private void addMenusToMeal(Meal meal, Restaurant restaurant, List<String> menuNames) {
        for (String menuName : menuNames) {
            Menu menu = menuService.createOrGetMenu(menuName, restaurant);
            createMealMenu(meal, menu);
        }
    }

    private void createMealMenu(Meal meal, Menu menu) {
        MealMenu mealMenu = MealMenu.builder()
            .menu(menu)
            .meal(meal)
            .build();

        mealMenuRepository.save(mealMenu);
    }

    @Transactional
    public void deleteByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId).orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));

        List<Menu> menus = meal.getMealMenus().stream()
            .map(MealMenu::getMenu)
            .toList();

        mealRepository.delete(meal);
        mealRepository.flush();

        deleteUnusedMenus(menus);
    }

    private void deleteUnusedMenus(List<Menu> menus) {
        menus.stream()
            .filter(menu -> menu.getMealMenus().isEmpty())
            .forEach(menuService::deleteMenu);
    }
}
