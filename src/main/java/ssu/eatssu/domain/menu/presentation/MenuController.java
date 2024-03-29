package ssu.eatssu.domain.menu.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.menu.dto.CategoryMenuListCollection;
import ssu.eatssu.domain.menu.service.MenuService;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.restaurant.entity.RestaurantType;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponse;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_SUPPORT_RESTAURANT;

@RestController
@RequiredArgsConstructor
@RequestMapping("/menus")
@Tag(name = "Menu", description = "메뉴 API")
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "고정 메뉴 리스트 조회", description = """
            고정 메뉴 리스트를 조회하는 API 입니다.
            메뉴가 고정된 식당(푸드코트, 스낵코너, <s>더 키친</s>)의 메뉴 리스트를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 식당(변동 메뉴 식당)", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식당", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("")
    public BaseResponse<CategoryMenuListCollection> getMenus(
            @RequestParam("restaurant") Restaurant restaurant) {
        if (RestaurantType.isVariableType(restaurant)) {
            throw new BaseException(NOT_SUPPORT_RESTAURANT);
        }
        return BaseResponse.success(menuService.findMenusByRestaurant(restaurant));
    }


}
