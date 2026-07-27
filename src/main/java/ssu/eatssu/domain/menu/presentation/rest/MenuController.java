package ssu.eatssu.domain.menu.presentation.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.menu.presentation.dto.response.MenuRestaurantResponse;
import ssu.eatssu.domain.menu.presentation.rest.docs.MenuControllerDocs;
import ssu.eatssu.domain.menu.service.MenuService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
public class MenuController implements MenuControllerDocs {

    private final MenuService menuService;

    @Override
    @GetMapping
    public BaseResponse<MenuRestaurantResponse> getMenus(
            @RequestParam("restaurant") Restaurant restaurant,
            @RequestParam(required = false) Language language) {

        return BaseResponse.success(menuService.getMenusByRestaurant(restaurant, language));
    }
}
