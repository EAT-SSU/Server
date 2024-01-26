package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.*;
import ssu.eatssu.domain.admin.persistence.LoadMealRepository;
import ssu.eatssu.domain.admin.persistence.MenuRatingRepository;
import ssu.eatssu.domain.menu.entity.TimePart;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ManageMealService {

    private final LoadMealRepository loadMealRepository;
    private final MenuRatingRepository menuRatingRepository;

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

}