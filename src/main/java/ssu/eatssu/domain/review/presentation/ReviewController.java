package ssu.eatssu.domain.review.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.constants.MenuType;
import ssu.eatssu.domain.review.dto.MealReviewsResponse;
import ssu.eatssu.domain.review.dto.MenuReviewResponse;
import ssu.eatssu.domain.review.dto.ReviewCreateRequest;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.ReviewDetailV1;
import ssu.eatssu.domain.review.dto.ReviewUpdateRequest;
import ssu.eatssu.domain.review.dto.SavedReviewImage;
import ssu.eatssu.domain.review.dto.UploadReviewRequest;
import ssu.eatssu.domain.review.presentation.docs.ReviewControllerDocs;
import ssu.eatssu.domain.review.service.ReviewService;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.slice.service.SliceService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController implements ReviewControllerDocs {

    private final ReviewService reviewService;
    private final SliceService sliceService;

    @Override
    @GetMapping("")
    public BaseResponse<SliceResponse<ReviewDetailV1>> getReviews(
            @RequestParam("menuType") MenuType menuType,
            @RequestParam(value = "menuId", required = false) Long menuId,
            @RequestParam(value = "mealId", required = false) Long mealId,
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<ReviewDetailV1> myReviews = sliceService.findReviewsV1(menuType, menuId, mealId,
                                                                         pageable, lastReviewId, customUserDetails);

        return BaseResponse.success(myReviews);
    }

    @Override
    @PostMapping(value = "/{menuId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<?> writeReview(
            @PathVariable("menuId") Long menuId,
            @RequestPart ReviewCreateRequest createReviewRequest,
            @RequestPart(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.createReview(customUserDetails, menuId, createReviewRequest,
                                   multipartFileList);
        return BaseResponse.success();
    }

    /**
     * 리뷰 이미지 업로드
     */
    @Override
    @PostMapping(value = "/upload/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<SavedReviewImage> uploadReviewImage(@RequestPart(value = "image") MultipartFile image) {
        return BaseResponse.success(reviewService.uploadImage(image));
    }

    @Override
    @PostMapping("/write/{menuId}")
    public BaseResponse<?> writeReview(@PathVariable("menuId") Long menuId,
                                       @RequestBody UploadReviewRequest uploadReviewRequest,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.uploadReview(customUserDetails, menuId, uploadReviewRequest);
        return BaseResponse.success();
    }

    @Override
    @PatchMapping("/{reviewId}")
    public BaseResponse<?> updateReview(@PathVariable("reviewId") Long reviewId,
                                        @RequestBody ReviewUpdateRequest request,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.updateReview(customUserDetails, reviewId, request);
        return BaseResponse.success();
    }

    @Override
    @DeleteMapping("/{reviewId}")
    public BaseResponse<?> deleteReview(
            @PathVariable("reviewId") Long reviewId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.deleteReview(customUserDetails, reviewId);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("/meals/{mealId}")
    public BaseResponse<MealReviewsResponse> getMealReviews(
            @PathVariable(value = "mealId") Long mealId) {
        return BaseResponse.success(reviewService.findMealReviews(mealId));
    }

    @Override
    @GetMapping("/menus/{menuId}")
    public BaseResponse<MenuReviewResponse> getMainReviews(
            @PathVariable(value = "menuId") Long menuId) {
        return BaseResponse.success(reviewService.findMenuReviews(menuId));
    }

}
