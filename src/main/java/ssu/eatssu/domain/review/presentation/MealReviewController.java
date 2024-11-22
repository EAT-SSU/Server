package ssu.eatssu.domain.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.service.MealReviewService;
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
}
