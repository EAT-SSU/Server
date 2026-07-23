package ssu.eatssu.domain.review.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ssu.eatssu.domain.review.dto.CreateMenuReviewRequestV2;
import ssu.eatssu.domain.review.dto.MealReviewResponse;
import ssu.eatssu.domain.review.dto.MealReviewsV2Response;
import ssu.eatssu.domain.review.dto.MenuReviewsV2Response;
import ssu.eatssu.domain.review.dto.RestaurantReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.ReviewTranslationResponse;
import ssu.eatssu.domain.review.dto.UpdateMealReviewRequest;
import ssu.eatssu.domain.review.dto.ValidMenuForViewResponse;
import ssu.eatssu.domain.review.presentation.docs.ReviewControllerV2Docs;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.review.service.ReviewTranslationService;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.MyMealReviewResponse;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/reviews")
public class ReviewControllerV2 implements ReviewControllerV2Docs {
    private final ReviewServiceV2 reviewServiceV2;
    private final ReviewTranslationService reviewTranslationService;

    @Override
    @PostMapping("/meal")
    public BaseResponse<?> createMealReview(
            @Valid @RequestBody CreateMealReviewRequest createMealReviewRequest,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewServiceV2.createMealReview(customUserDetails, createMealReviewRequest);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("/statistics/restaurant")
    public BaseResponse<RestaurantReviewResponse> getRestaurantReviews(
            @RequestParam Restaurant restaurant
                                                                      ) {
        return BaseResponse.success(reviewServiceV2.findRestaurantReviews(restaurant));
    }

    @Override
    @GetMapping("/list/meal")
    public BaseResponse<SliceResponse<MealReviewResponse>> getMealReviewList(
            @RequestParam Long mealId,
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"));
        SliceResponse<MealReviewResponse> myReviews = reviewServiceV2.findMealReviewList(mealId, lastReviewId, pageable,
                                                                                         customUserDetails);
        return BaseResponse.success(myReviews);
    }

    @Override
    @PatchMapping("/{reviewId}")
    public BaseResponse<?> updateReview(@PathVariable("reviewId") Long reviewId,
                                        @RequestBody UpdateMealReviewRequest request,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewServiceV2.updateReview(customUserDetails, reviewId, request);
        return BaseResponse.success();
    }

    @Override
    @DeleteMapping("/{reviewId}")
    public BaseResponse<?> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewServiceV2.deleteReview(customUserDetails, reviewId);
        return BaseResponse.success();
    }

    @Override
    @PostMapping("/{reviewId}/translate")
    public BaseResponse<ReviewTranslationResponse> translateReview(
            @PathVariable("reviewId") Long reviewId,
            @RequestParam Language language) {
        return BaseResponse.success(reviewTranslationService.translateReview(reviewId, language));
    }

    @Override
    @GetMapping("/statistics/meals/{mealId}")
    public BaseResponse<MealReviewsV2Response> getMealReviews(
            @PathVariable(value = "mealId") Long mealId) {
        return BaseResponse.success(reviewServiceV2.findMealReviews(mealId));
    }

    @Override
    @GetMapping("/statistics/menus/{menuId}")
    public BaseResponse<MenuReviewsV2Response> getMainReviews(
            @PathVariable(value = "menuId") Long menuId) {
        return BaseResponse.success(reviewServiceV2.findMenuReviews(menuId));
    }

    @Override
    @GetMapping("/list/menu")
    public BaseResponse<SliceResponse<ReviewDetail>> getMenuReviewList(
            @RequestParam(value = "menuId") Long menuId,
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<ReviewDetail> myReviews = reviewServiceV2.findMenuReviewList(menuId,
                                                                                   pageable,
                                                                                   lastReviewId,
                                                                                   customUserDetails);

        return BaseResponse.success(myReviews);
    }

    @Override
    @PostMapping("/menu")
    public BaseResponse<?> createMenuReview(@Valid @RequestBody CreateMenuReviewRequestV2 createMenuReviewRequestV2,
                                            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewServiceV2.createMenuReview(customUserDetails, createMenuReviewRequestV2);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("/my")
    public BaseResponse<SliceResponse<MyMealReviewResponse>> getMyReviews(
            @RequestParam(required = false) Long lastReviewId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<MyMealReviewResponse> myReviews = reviewServiceV2.findMyReviews(customUserDetails,
                                                                                      lastReviewId,
                                                                                      pageable);
        return BaseResponse.success(myReviews);
    }

    @Override
    @GetMapping("/meal/valid-for-review/{mealId}")
    public BaseResponse<ValidMenuForViewResponse> getValidMenuForReview(
            @PathVariable("mealId") Long mealId) {
        ValidMenuForViewResponse validMenuForViewResponse = reviewServiceV2.validMenuForReview(mealId);
        return BaseResponse.success(validMenuForViewResponse);
    }


}
