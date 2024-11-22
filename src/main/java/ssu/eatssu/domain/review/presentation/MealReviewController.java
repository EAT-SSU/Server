package ssu.eatssu.domain.review.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
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

}
