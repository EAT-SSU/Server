package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.admin.dto.*;
import ssu.eatssu.domain.admin.persistence.FixMenuRatingRepository;
import ssu.eatssu.domain.admin.persistence.ManageFixMenuRepository;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageFixMenuService {

    private final ManageFixMenuRepository manageFixMenuRepository;
    private final FixMenuRatingRepository fixMenuRatingRepository;

    public MenuBoards getMenuBoards() {
        MenuBoards menuBoards = new MenuBoards();

        RestaurantType.FIXED.getRestaurants()
                .forEach(restaurant -> menuBoards.add(getRestaurantMenuBoard(restaurant)));

        return menuBoards;
    }

    private RestaurantMenuBoard getRestaurantMenuBoard(RestaurantName restaurant) {
        FixMenuBoard menuBoard = new FixMenuBoard(restaurant.getDescription());

        List<BriefMenu> briefMenus = manageFixMenuRepository.findBriefMenusByRestaurantName(restaurant);

        briefMenus.forEach(briefMenu ->
                menuBoard.addMenu(new MenuLine(
                        briefMenu.id(),
                        briefMenu.name(),
                        briefMenu.price(),
                        fixMenuRatingRepository.getMainRatingAverage(briefMenu.id())
                ))
        );

        return menuBoard;
    }
}
