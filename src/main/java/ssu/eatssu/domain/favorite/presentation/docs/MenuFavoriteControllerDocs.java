package ssu.eatssu.domain.favorite.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.favorite.dto.response.FavoriteMenuResponse;
import ssu.eatssu.domain.favorite.dto.response.MenuSearchResponse;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@Tag(name = "MenuFavorite", description = "메뉴 찜 API")
public interface MenuFavoriteControllerDocs {

    @Operation(summary = "찜할 메뉴 검색", description = """
            메뉴명으로 찜할 메뉴를 검색하는 API 입니다.<br><br>
            검색어는 앞뒤 공백을 제거한 뒤 두 글자 이상이어야 하며, 대소문자를 구분하지 않습니다.<br>
            단종된 메뉴는 제외되고, 완전히 일치 → 검색어로 시작 → 검색어 포함 순으로 정렬됩니다.<br>
            같은 메뉴명이라도 식당이 다르면 따로 노출됩니다.<br>
            사용자 언어의 번역이 있으면 번역된 이름으로도 검색되고, 없으면 한국어 이름을 반환합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 검색 성공"),
            @ApiResponse(responseCode = "400", description = "두 글자 미만 검색어", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<List<MenuSearchResponse>> searchMenus(@Parameter(description = "검색어", example = "돈까스")
                                                       String keyword,
                                                       CustomUserDetails userDetails);

    @Operation(summary = "찜한 메뉴 목록 조회", description = "내가 찜한 메뉴를 최근에 찜한 순으로 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "찜한 메뉴 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<List<FavoriteMenuResponse>> getFavoriteMenus(CustomUserDetails userDetails);

    @Operation(summary = "메뉴 찜하기", description = "메뉴를 찜 목록에 추가하는 API 입니다. 이미 찜한 메뉴면 아무 변화 없이 성공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 찜 성공"),
            @ApiResponse(responseCode = "400", description = "단종된 메뉴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저 또는 메뉴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> addFavorite(Long menuId, CustomUserDetails userDetails);

    @Operation(summary = "메뉴 찜 취소", description = "메뉴를 찜 목록에서 삭제하는 API 입니다. 찜하지 않은 메뉴여도 성공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 찜 취소 성공"),
    })
    BaseResponse<?> removeFavorite(Long menuId, CustomUserDetails userDetails);
}
