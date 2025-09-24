package ssu.eatssu.domain.menu.service;

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
import ssu.eatssu.domain.menu.presentation.dto.request.CreateMealRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

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
        Meal meal = mealRepository.findById(mealId)
                                  .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));
        List<Menu> menus = meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .toList();

        if (menus.isEmpty()) {
            log.warn("Meal[{}] has no menus.", mealId);
        }

        return MenusInMealResponse.from(menus);
    }

    public List<MealDetailResponse> getMealDetailsByDateAndRestaurantAndTimePart(
            Date date, Restaurant restaurant, TimePart timePart) {

        validateMealRestaurant(restaurant);

        List<Meal> meals = findMealsByDateAndTimePartAndRestaurant(date, timePart, restaurant);

        return meals.stream()
                    .map(meal -> MealDetailResponse.from(meal,
                                                         mealRatingService.getMainRatingAverage(meal.getId())))
                    .toList();
    }

    private List<Meal> findMealsByDateAndTimePartAndRestaurant(Date date, TimePart timePart,
                                                               Restaurant restaurant) {
        return mealRepository.findAllByDateAndTimePartAndRestaurant(date, timePart, restaurant);
    }

    private void validateMealRestaurant(Restaurant restaurant) {
        if (RestaurantType.isFixedType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
    }

    @Transactional
    public Long createMeal(Date date, Restaurant restaurant, TimePart timePart,
                           CreateMealRequest request) {
        return createMealWithOptionalPrice(date, restaurant, timePart, request.menuNames(), null);
    }

    @Transactional
    public Long createMealWithPrice(Date date, Restaurant restaurant, TimePart timePart,
                                    MealCreateWithPriceRequest request) {
        return createMealWithOptionalPrice(date, restaurant, timePart, request.menuNames(), request.price());
    }

    private Long createMealWithOptionalPrice(Date date, Restaurant restaurant, TimePart timePart,
                                             List<String> menuNames, Integer price) {

        Optional<Long> existingMealId = getExistingMealId(date, timePart, restaurant, menuNames);
        if (existingMealId.isPresent()) {
            return existingMealId.get();  // 이미 존재하는 식단의 ID를 반환
        }

        Meal meal = new Meal(date, timePart, restaurant, price);
        Meal savedMeal = mealRepository.save(meal);
        addMenusToMeal(meal, restaurant, menuNames);

        return savedMeal.getId();
    }

    private Optional<Long> getExistingMealId(Date date, TimePart timePart, Restaurant restaurant,
                                             List<String> menuNames) {
        List<Meal> meals = mealRepository.findAllByDateAndTimePartAndRestaurant(date, timePart, restaurant);

        List<String> sortedRequestMenuNames = menuNames.stream()
                                                       .sorted()
                                                       .toList();

        return meals.stream()
                    .filter(meal -> {
                        List<String> sortedMenuNames = meal.getMenuNames().stream()
                                                           .sorted()
                                                           .toList();
                        return sortedMenuNames.equals(sortedRequestMenuNames);
                    })
                    .findFirst()
                    .map(Meal::getId);
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
        meal.addMealMenu(mealMenu);
    }

    @Transactional
    public void deleteByMealId(Long mealId) {
        Meal meal = mealRepository.findById(mealId)
                                  .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));

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
