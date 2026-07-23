package ssu.eatssu.domain.partnership.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.dto.PartnershipResponse;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@Tag(name = "Partnership", description = "제휴 API")
public interface PartnershipControllerDocs {

    @Operation(summary = "제휴 등록", description = "제휴를 등록하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 등록 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 대학", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 학과", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> createPartnership(CreatePartnershipRequest request);

    @Operation(summary = "식당별 전체 제휴 조회", description = "식당별 전체 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 조회 성공"),
    })
    BaseResponse<List<PartnershipResponse>> getAllPartnerships(CustomUserDetails userDetails);

    @Operation(summary = "제휴 찜 등록하기/취소하기", description = "제휴 찜 등록하기/취소하기(토글) API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 찜 등록/취소 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 제휴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> togglePartnershipLike(Long partnershipId, CustomUserDetails userDetails);
}
