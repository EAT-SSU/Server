package ssu.eatssu.domain.favorite.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.favorite.dto.response.FavoriteMenuResponse;
import ssu.eatssu.domain.favorite.dto.response.MenuSearchResponse;
import ssu.eatssu.domain.favorite.presentation.docs.MenuFavoriteControllerDocs;
import ssu.eatssu.domain.favorite.service.MenuFavoriteService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequestMapping("/menu-favorites")
@RequiredArgsConstructor
public class MenuFavoriteController implements MenuFavoriteControllerDocs {

    private final MenuFavoriteService menuFavoriteService;

    @Override
    @GetMapping("/search")
    public BaseResponse<List<MenuSearchResponse>> searchMenus(@RequestParam String keyword,
                                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(menuFavoriteService.searchMenus(userDetails, keyword));
    }

    @Override
    @GetMapping
    public BaseResponse<List<FavoriteMenuResponse>> getFavoriteMenus(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(menuFavoriteService.getFavoriteMenus(userDetails));
    }

    @Override
    @PostMapping("/{menuId}")
    public BaseResponse<?> addFavorite(@PathVariable Long menuId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        menuFavoriteService.addFavorite(userDetails, menuId);
        return BaseResponse.success();
    }

    @Override
    @DeleteMapping("/{menuId}")
    public BaseResponse<?> removeFavorite(@PathVariable Long menuId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        menuFavoriteService.removeFavorite(userDetails, menuId);
        return BaseResponse.success();
    }
}
