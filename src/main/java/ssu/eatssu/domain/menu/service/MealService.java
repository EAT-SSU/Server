package ssu.eatssu.domain.menu.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMainMenu;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.menu.persistence.MealMainMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealMenuRepository;
import ssu.eatssu.domain.menu.persistence.MealRepository;
import ssu.eatssu.domain.menu.presentation.dto.request.CreateMealRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MainMenuRequest;
import ssu.eatssu.domain.menu.presentation.dto.request.MealCreateWithPriceRequest;
import ssu.eatssu.domain.menu.presentation.dto.response.MealCreateResult;
import ssu.eatssu.domain.menu.presentation.dto.response.MealDetailResponse;
import ssu.eatssu.domain.menu.presentation.dto.response.MenusInMealResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MealService {

    private final MealRepository mealRepository;
    private final MealMenuRepository mealMenuRepository;
    private final MealMainMenuRepository mealMainMenuRepository;
    private final MealRatingService mealRatingService;
    private final MenuService menuService;

    public MenusInMealResponse getMenusInMealByMealId(Long mealId, Language language) {
        Meal meal = mealRepository.findById(mealId)
                                  .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MEAL));
        List<Menu> menus = meal.getMealMenus().stream()
                .map(MealMenu::getMenu)
                .toList();

        if (menus.isEmpty()) {
            log.warn("Meal[{}] has no menus.", mealId);
        }

        return MenusInMealResponse.from(menus, getMainMenuTranslations(mealId, language));
    }

    public List<MealDetailResponse> getMealDetailsByDateAndRestaurantAndTimePart(
            Date date, Restaurant restaurant, TimePart timePart, Language language) {

        validateMealRestaurant(restaurant);

        List<Meal> meals = findMealsByDateAndTimePartAndRestaurant(date, timePart, restaurant);

        return meals.stream()
                    .map(meal -> MealDetailResponse.from(meal,
                                                         mealRatingService.getMainRatingAverage(meal.getId()),
                                                         getMainMenuTranslations(meal.getId(), language)))
                    .toList();
    }

    private Map<String, String> getMainMenuTranslations(Long mealId, Language language) {
        if (language != Language.EN) {
            return Map.of();
        }

        return mealMainMenuRepository.findAllByMeal_Id(mealId).stream()
                                     .collect(Collectors.toMap(MealMainMenu::getNameKo, MealMainMenu::getNameEn));
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
    public MealCreateResult createMeal(Date date, Restaurant restaurant, TimePart timePart,
                                       CreateMealRequest request) {
        return createMealWithOptionalPrice(date, restaurant, timePart, request.menuNames(), null,
                                           request.mainMenus());
    }

    @Transactional
    public MealCreateResult createMealWithPrice(Date date, Restaurant restaurant, TimePart timePart,
                                                MealCreateWithPriceRequest request) {
        return createMealWithOptionalPrice(date, restaurant, timePart, request.menuNames(), request.price(),
                                           request.mainMenus());
    }

    private MealCreateResult createMealWithOptionalPrice(Date date, Restaurant restaurant, TimePart timePart,
                                                          List<String> menuNames, Integer price,
                                                          List<MainMenuRequest> mainMenus) {

        Long mealId = getExistingMealId(date, timePart, restaurant, menuNames)
                .orElseGet(() -> createNewMeal(date, restaurant, timePart, menuNames, price));

        List<String> unmatchedMainMenus = upsertMainMenus(mealId, menuNames, mainMenus);

        return new MealCreateResult(mealId, unmatchedMainMenus);
    }

    private Long createNewMeal(Date date, Restaurant restaurant, TimePart timePart, List<String> menuNames,
                               Integer price) {
        Meal meal = new Meal(date, timePart, restaurant, price);
        Meal savedMeal = mealRepository.save(meal);
        addMenusToMeal(meal, restaurant, menuNames);

        return savedMeal.getId();
    }

    private List<String> upsertMainMenus(Long mealId, List<String> menuNames, List<MainMenuRequest> mainMenus) {
        if (mainMenus == null) {
            return List.of();
        }

        mealMainMenuRepository.deleteAllByMeal_Id(mealId);
        mealMainMenuRepository.flush();

        if (mainMenus.isEmpty()) {
            return List.of();
        }

        Meal meal = mealRepository.getReferenceById(mealId);
        List<String> unmatchedMainMenus = new ArrayList<>();
        int seq = 0;
        for (MainMenuRequest mainMenu : mainMenus) {
            if (!menuNames.contains(mainMenu.nameKo())) {
                log.warn("mainMenus.nameKo가 menuNames와 매칭되지 않음. mealId={}, nameKo={}", mealId, mainMenu.nameKo());
                unmatchedMainMenus.add(mainMenu.nameKo());
                continue;
            }
            mealMainMenuRepository.save(MealMainMenu.of(meal, seq++, mainMenu.nameKo(), mainMenu.nameEn()));
        }

        return unmatchedMainMenus;
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
