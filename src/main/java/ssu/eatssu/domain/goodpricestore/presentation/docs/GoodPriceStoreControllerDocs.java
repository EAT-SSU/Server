package ssu.eatssu.domain.goodpricestore.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreDetailResponse;
import ssu.eatssu.domain.goodpricestore.dto.response.GoodPriceStoreResponse;
import ssu.eatssu.domain.goodpricestore.entity.CategoryType;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@Tag(name = "GoodPriceStore", description = "착한가격업소 API")
public interface GoodPriceStoreControllerDocs {

    @Operation(summary = "착한가격업소 목록 조회", description = "업종 필터로 착한가격업소 핀 목록을 조회하는 API 입니다. 비로그인 사용자도 호출할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "착한가격업소 목록 조회 성공"),
    })
    BaseResponse<List<GoodPriceStoreResponse>> getStores(
            @Parameter(description = "업종 필터, 미지정 시 전체 조회") CategoryType category);

    @Operation(summary = "착한가격업소 상세 조회", description = "업소 상세 정보를 조회하는 API 입니다. 비로그인 사용자도 호출할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "착한가격업소 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 착한가격업소", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<GoodPriceStoreDetailResponse> getStoreDetail(Long id);
}
