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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.CreateMenuReviewRequest;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.ValidMenuForViewResponse;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/reviews")
@Tag(name = "Review V2", description = "리뷰 V2 API")
public class ReviewControllerV2 {
    private final ReviewServiceV2 reviewServiceV2;

    @Operation(summary = "meal(식단)에 대한 리뷰 작성", description = "리뷰를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/meal")
    public BaseResponse<?> createMealReview(
            @RequestBody CreateMealReviewRequest createMealReviewRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewServiceV2.createMealReview(customUserDetails, createMealReviewRequest);
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
    @GetMapping("/statistics/restaurant")
    public BaseResponse<RestaurantReviewResponse> getRestaurantReviews(
            @Parameter(description = "restaurant")
            @RequestParam Restaurant restaurant
                                                                      ) {
        return BaseResponse.success(reviewServiceV2.findRestaurantReviews(restaurant));
    }

    @Operation(summary = "meal(식단)에 대한 리뷰 리스트 조회", description = """
            리뷰 리스트를 조회하는 API 입니다.<br><br>
            커서 기반 페이지네이션으로 리뷰 리스트를 조회합니다.<br><br>
            페이징 기본 값 = {size=20, sort=date, direction=desc}<br><br>
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/list/meal")
    public BaseResponse<SliceResponse<MealReviewResponse>> getMealReviewList(
            @Parameter(description = "mealId")
            @RequestParam Long mealId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY)
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
        SliceResponse<MealReviewResponse> myReviews = reviewServiceV2.findMealReviewList(mealId, lastReviewId, pageable,
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
        reviewServiceV2.updateReview(customUserDetails, reviewId, request);
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
        reviewServiceV2.deleteReview(customUserDetails, reviewId);
        return BaseResponse.success();
    }

    @Operation(summary = "식단(변동 메뉴) 리뷰 정보 조회 V2(메뉴명, 평점 등등) [인증 토큰 필요 X]", description = """
            식단 리뷰 정보를 조회하는 API 입니다.<br><br>
            메뉴명 리스트, 리뷰 수, 메인 평점, 좋아요 개수, 싫어요 개수, 각 평점의 개수를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/statistics/meals/{mealId}")
    public BaseResponse<MealReviewsV2Response> getMealReviews(
            @Parameter(description = "mealId")
            @PathVariable(value = "mealId") Long mealId) {
        return BaseResponse.success(reviewServiceV2.findMealReviews(mealId));
    }

    @Operation(summary = "고정 메뉴 리뷰 정보 조회 V2(메뉴명, 평점 등등) [인증 토큰 필요 X]", description = """
            고정 메뉴 리뷰 정보를 조회하는 API 입니다.<br><br>
            메뉴명, 리뷰 수, 메인 평점, 좋아요 개수, 싫어요 개수, 각 평점의 개수를 조회합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/statistics/menus/{menuId}")
    public BaseResponse<MenuReviewsV2Response> getMainReviews(
            @Parameter(description = "menuId")
            @PathVariable(value = "menuId") Long menuId) {
        return BaseResponse.success(reviewServiceV2.findMenuReviews(menuId));
    }

    @Operation(summary = "menu 에 대한 리뷰 리스트 조회", description = """
            리뷰 리스트를 조회하는 API 입니다.<br><br>
            커서 기반 페이지네이션으로 리뷰 리스트를 조회합니다.<br><br>
            페이징 기본 값 = {size=20, sort=date, direction=desc}<br><br>
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/list/menu")
    public BaseResponse<SliceResponse<ReviewDetail>> getMenuReviewList(
            @Parameter(description = "menuId(고정메뉴)") @RequestParam(value = "menuId") Long menuId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY)
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<ReviewDetail> myReviews = reviewServiceV2.findMenuReviewList(menuId,
                                                                                   pageable,
                                                                                   lastReviewId,
                                                                                   customUserDetails);

        return BaseResponse.success(myReviews);
    }

    @Operation(summary = "menu에 대한 리뷰 작성", description = "리뷰를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/menu")
    public BaseResponse<?> createMenuReview(@RequestBody CreateMenuReviewRequest createMenuReviewRequest,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewServiceV2.createMenuReview(customUserDetails, createMenuReviewRequest);
        return BaseResponse.success();
    }

    @Operation(summary = "내가 쓴 리뷰 리스트 조회", description = "내가 쓴 리뷰 리스트를 조회하는 API V2 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 쓴 리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/my")
    public BaseResponse<SliceResponse<MyMealReviewResponse>> getMyReviews(
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) @RequestParam(required = false) Long lastReviewId,
            @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<MyMealReviewResponse> myReviews = reviewServiceV2.findMyReviews(customUserDetails,
                                                                                      lastReviewId,
                                                                                      pageable);
        return BaseResponse.success(myReviews);
    }

    @Operation(summary = "식단 id를 통해 리뷰 작성할 수 있는 메뉴들 조회", description = "리뷰 작성할 수 있는 메뉴들 조회하는 API입니다. (노션 문서 > 리뷰v2 기능명세서> 리뷰에 제외되는 메뉴 참고")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 작성할 수 있는 메뉴들 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/meal/valid-for-review/{mealId}")
    public BaseResponse<ValidMenuForViewResponse> getValidMenuForReview(
            @Parameter(description = "mealId")
            @PathVariable("mealId") Long mealId) {
        ValidMenuForViewResponse validMenuForViewResponse = reviewServiceV2.validMenuForReview(mealId);
        return BaseResponse.success(validMenuForViewResponse);
    }


}
