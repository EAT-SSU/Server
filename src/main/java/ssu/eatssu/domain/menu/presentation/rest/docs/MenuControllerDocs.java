package ssu.eatssu.domain.menu.presentation.rest.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.menu.presentation.dto.response.MenuRestaurantResponse;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseResponse;

@Tag(name = "Menu", description = "메뉴 API")
public interface MenuControllerDocs {

    @Operation(summary = "고정 메뉴 리스트 조회 [인증 토큰 필요 X]",
            description =
                    """
                            고정 메뉴 리스트를 조회하는 API 입니다.
                            메뉴가 고정된 식당(푸드코트, 스낵코너, <s>더 키친</s>)의 메뉴 리스트를 조회합니다.
                            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 식당(변동 메뉴 식당)", content =
            @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식당", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<MenuRestaurantResponse> getMenus(Restaurant restaurant, Language language);
}
