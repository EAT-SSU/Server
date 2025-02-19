package ssu.eatssu.domain.partnership.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.CreatePartnershipRequest;
import ssu.eatssu.domain.partnership.dto.PartnershipDetailResponse;
import ssu.eatssu.domain.partnership.dto.PartnershipResponse;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequestMapping("/partnerships")
@RequiredArgsConstructor
@Tag(name = "Partnership", description = "제휴 API")
public class PartnershipController {
    private final PartnershipService partnershipService;

    @Operation(summary = "제휴 등록", description = "제휴를 등록하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 등록 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 대학", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 학과", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping
    public BaseResponse<?> createPartnership(@RequestBody CreatePartnershipRequest request) {
        partnershipService.createPartnership(request);
        return BaseResponse.success();
    }

    @Operation(summary = "전체 제휴 조회", description = "전체 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 조회 성공"),
    })
    @GetMapping
    public BaseResponse<List<PartnershipResponse>> getAllPartnerships() {
        return BaseResponse.success(partnershipService.getAllPartnerships());
    }

    @Operation(summary = "개별 제휴 조회", description = "개별 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 제휴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/{partnershipId}")
    public BaseResponse<PartnershipDetailResponse> getPartnership(
            @PathVariable Long partnershipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getPartnership(partnershipId, userDetails));
    }

    @Operation(summary = "제휴 찜 등록하기/취소하기", description = "제휴 찜 등록하기/취소하기(토글) API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제휴 찜 등록/취소 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 제휴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/{partnershipId}/like")
    public BaseResponse<?> togglePartnershipLike(
            @PathVariable Long partnershipId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        partnershipService.togglePartnershipLike(partnershipId, userDetails);
        return BaseResponse.success();
    }
}
