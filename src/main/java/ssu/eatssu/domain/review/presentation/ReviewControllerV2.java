package ssu.eatssu.domain.review.presentation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.review.dto.CreateMealReviewRequest;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuReviewResponse;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.review.service.ReviewService;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/reviews")
@Tag(name = "Review V2", description = "리뷰 V2 API")
public class ReviewControllerV2 {
	private final ReviewServiceV2 reviewServiceV2;

	private final ReviewService reviewService;

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
		reviewServiceV2.createReview(customUserDetails, createMealReviewRequest);
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
		return BaseResponse.success(reviewServiceV2.findRestaurantReviews(restaurant));
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
		SliceResponse<MealReviewResponse> myReviews = reviewServiceV2.findReviews(mealId, lastReviewId, pageable,
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
	@GetMapping("/meals/{mealId}")
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
	@GetMapping("/menus/{menuId}")
	public BaseResponse<MenuReviewsV2Response> getMainReviews(
		@Parameter(description = "menuId")
		@PathVariable(value = "menuId") Long menuId) {
		return BaseResponse.success(reviewServiceV2.findMenuReviews(menuId));
	}

}
