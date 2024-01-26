package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.*;
import ssu.eatssu.domain.admin.persistence.*;
import ssu.eatssu.domain.menu.entity.Meal;
import ssu.eatssu.domain.menu.entity.MealMenu;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Date;
import java.util.List;

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
                .forEach(restaurant -> menuBoards.add(getMealBoard(new MealInfo(restaurant, date, timePart))));

        return menuBoards;
    }

    private MenuBoard getMealBoard(MealInfo mealInfo) {
        MenuBoard menuBoard = new MenuBoard(mealInfo.restaurant().getDescription());

        getMenuSections(mealInfo).forEach(menuBoard::addMenuSection);

        return menuBoard;
    }

    private List<MenuSection> getMenuSections(MealInfo mealInfo) {
        List<Long> mealIds = loadMealRepository.getAllMealIdsByInfo(mealInfo);

        return mealIds.stream().map(this::getMenuSection).toList();
    }

    private MenuSection getMenuSection(Long mealId) {
        MenuSection menuSection = new MenuSection();

        List<BriefMenu> briefMenus = loadMealRepository.findBriefMenusByMealId(mealId);

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
}