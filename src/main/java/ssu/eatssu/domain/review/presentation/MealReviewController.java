package ssu.eatssu.domain.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.service.MealReviewService;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/reviews")
@Tag(name = "Review V2", description = "리뷰 V2 API")
public class MealReviewController {
    private final MealReviewService mealReviewService;

    @Operation(summary = "리뷰 작성", description = "리뷰를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping()
    public BaseResponse<?> createReview(
            @RequestBody CreateMealReviewRequest createMealReviewRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        mealReviewService.createReview(customUserDetails, createMealReviewRequest);
        return BaseResponse.success();
    }

    @Operation(summary = "특정 식당 모든 리뷰 정보 조회(리뷰 개수, 평점 등등)", description = """
            특정 식당 모든 리뷰 정보를 조회하는 API 입니다.<br><br>
            리뷰 개수, 별점 별 개수, 리뷰 평점, 좋아요 개수, 싫어요 개수를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "path parameter 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/statistics")
    public BaseResponse<RestaurantReviewResponse> getRestaurantReviews(
            @Parameter(description = "restaurant")
            @RequestParam Restaurant restaurant
    ) {
        return BaseResponse.success(mealReviewService.findRestaurantReviews(restaurant));
    }

    @Operation(summary = "리뷰 리스트 조회", description = """
            리뷰 리스트를 조회하는 API 입니다.<br><br>
            커서 기반 페이지네이션으로 리뷰 리스트를 조회합니다.<br><br>
            페이징 기본 값 = {size=20, sort=date, direction=desc}<br><br>
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("")
    public BaseResponse<SliceResponse<MealReviewResponse>> getReviews(
            @Parameter(description = "mealId")
            @RequestParam Long mealId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY)
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
        SliceResponse<MealReviewResponse> myReviews = mealReviewService.findReviews(mealId, lastReviewId, pageable,
                customUserDetails);
        return BaseResponse.success(myReviews);
    }

    @Operation(summary = "리뷰 수정(글 수정)", description = """
            리뷰 내용을 수정하는 API 입니다.<br><br>
            글 수정만 가능하며 사진 수정은 지원하지 않습니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 수정 성공"),
            @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/{reviewId}")
    public BaseResponse<?> updateReview(@Parameter(description = "reviewId")
                                        @PathVariable("reviewId") Long reviewId,
                                        @RequestBody UpdateMealReviewRequest request,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        mealReviewService.updateReview(customUserDetails, reviewId, request);
        return BaseResponse.success();
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공"),
            @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{reviewId}")
    public BaseResponse<?> deleteReview(
            @Parameter(description = "reviewId") @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        mealReviewService.deleteReview(customUserDetails, reviewId);
        return BaseResponse.success();
    }

    @Operation(summary = "리뷰 좋아요 누르기/취소하기", description = "리뷰에 좋아요(찜)를 누르거나 취소하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 좋아요 토글 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/{reviewId}/like")
    public BaseResponse<?> toggleReviewLike(
            @Parameter(description = "reviewId") @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        mealReviewService.toggleReviewLike(customUserDetails, reviewId);
        return BaseResponse.success();
    }
}
