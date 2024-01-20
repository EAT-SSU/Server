package ssu.eatssu.domain.admin.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.admin.dto.*;
import ssu.eatssu.domain.admin.persistence.FindRestaurantRepository;
import ssu.eatssu.domain.admin.persistence.FixMenuRatingRepository;
import ssu.eatssu.domain.admin.persistence.LoadFixMenuRepository;
import ssu.eatssu.domain.admin.persistence.ManageMenuRepository;
import ssu.eatssu.domain.menu.entity.Menu;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantName;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class ManageFixMenuService {

    private final LoadFixMenuRepository loadMenuRepository;
    private final ManageMenuRepository manageMenuRepository;
    private final FixMenuRatingRepository fixMenuRatingRepository;
    private final FindRestaurantRepository findRestaurantRepository;

    public MenuBoards getMenuBoards() {
        MenuBoards menuBoards = new MenuBoards();

        RestaurantType.FIXED.getRestaurants()
                .forEach(restaurant -> menuBoards.add(getRestaurantMenuBoard(restaurant)));

        return menuBoards;
    }

    private RestaurantMenuBoard getRestaurantMenuBoard(RestaurantName restaurant) {
        FixMenuBoard menuBoard = new FixMenuBoard(restaurant.getDescription());

        List<BriefMenu> briefMenus = loadMenuRepository.findBriefMenusByRestaurantName(restaurant);

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

    public void register(RestaurantName restaurantName, RegisterFixMenuRequest request) {
        if (loadMenuRepository.existsMenu(request.name(), restaurantName)) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }
        Restaurant restaurant = findRestaurantRepository.findByRestaurantName(restaurantName);

        manageMenuRepository.save(Menu.createFixMenu(request.name(), restaurant, request.price()));
    }

    public void updateMenu(Long menuId, UpdateFixMenuRequest request) {
        if (isVariableMenu(menuId)) {
            throw new BaseException(BaseResponseStatus.NOT_SUPPORT_RESTAURANT);
        }

        Long restaurantId = loadMenuRepository.getRestaurantId(menuId);

        duplicateCheck(request.name(), restaurantId);

        Menu menu = manageMenuRepository.findById(menuId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_MENU));
        menu.update(request.name(), request.price());

        manageMenuRepository.save(menu);
    }

    private void duplicateCheck(String name, Long restaurantId) {
        if (loadMenuRepository.existsMenu(name, restaurantId)) {
            throw new BaseException(BaseResponseStatus.CONFLICT);
        }
    }

    private boolean isVariableMenu(Long menuId) {
        Long restaurantId = loadMenuRepository.getRestaurantId(menuId);
        Restaurant restaurant = findRestaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOT_FOUND_RESTAURANT));

        return RestaurantType.isVariableType(restaurant.getRestaurantName());
    }
}
