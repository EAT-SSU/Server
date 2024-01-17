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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.auth.entity.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.MenuType;
import ssu.eatssu.domain.review.dto.CreateReviewRequest;
import ssu.eatssu.domain.review.dto.MealReviewsResponse;
import ssu.eatssu.domain.review.dto.MainReviewsResponse;
import ssu.eatssu.domain.review.dto.ReviewDetail;
import ssu.eatssu.domain.review.dto.UpdateReviewRequest;
import ssu.eatssu.domain.slice.service.SliceService;
import ssu.eatssu.domain.review.service.ReviewService;
import ssu.eatssu.domain.slice.dto.SliceResponse;

import java.util.List;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final SliceService sliceService;

    @Operation(summary = "리뷰 리스트 조회", description = """
        리뷰 리스트를 조회하는 API 입니다.<br><br>
        menuType=FIX 의 경우 menuId 파라미터를 넣어주세요.<br><br>
        menuType=CHANGE 의 경우 mealId 파라미터를 넣어주세요.<br><br>
        커서 기반 페이지네이션으로 리뷰 리스트를 조회합니다.<br><br>
        페이징 기본 값 = {size=20, sort=date, direction=desc}<br><br>
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 리스트 조회 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("")
    public BaseResponse<SliceResponse<ReviewDetail>> getReviews(
        @Parameter(description = "타입(변동메뉴(식단)/고정메뉴)") @RequestParam("menuType") MenuType menuType,
        @Parameter(description = "menuId(고정메뉴)") @RequestParam(value = "menuId", required = false) Long menuId,
        @Parameter(description = "mealId(변동메뉴)") @RequestParam(value = "mealId", required = false) Long mealId,
        @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY)
        @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
        @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<ReviewDetail> myReviews = sliceService.findReviews(menuType, menuId, mealId,
            pageable, lastReviewId, customUserDetails);

        return BaseResponse.success(myReviews);
    }

    @Operation(summary = "리뷰 작성", description = """
        리뷰를 작성하는 API 입니다.<br><br>
        reviewCreate는 application/json, multipartFileList는 multipart/form-data로 요청해주세요.<br><br>
        사진은 여러장 첨부 가능합니다.(기획상으로는 한 장만 첨부하도록 제한이 있지만 API 스펙 자체는 여러 장 첨부 가능)
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 작성 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "500", description = "이미지 업로드 실패", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping(value = "/{menuId}",
        consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
        produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<?> writeReview(
        @Parameter(description = "menuId") @PathVariable("menuId") Long menuId,
        @RequestPart(value = "reviewCreate") CreateReviewRequest createReviewRequest,
        @RequestPart(value = "multipartFileList", required = false) List<MultipartFile> multipartFileList,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.writeReview(customUserDetails, menuId, createReviewRequest,
            multipartFileList);
        return BaseResponse.success();
    }

    @Operation(summary = "리뷰 수정(글 수정)", description = """
        리뷰 내용을 수정하는 API 입니다.<br><br>
        글 수정만 가능하며 사진 수정은 지원하지 않습니다.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 수정 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/{reviewId}")
    public BaseResponse<?> updateReview(@Parameter(description = "reviewId")
    @PathVariable("reviewId") Long reviewId,
        @RequestBody UpdateReviewRequest request,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.updateReview(customUserDetails, reviewId, request);
        return BaseResponse.success();
    }


    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제하는 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{reviewId}")
    public BaseResponse<?> deleteReview(
        @Parameter(description = "reviewId") @PathVariable("reviewId") Long reviewId,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        reviewService.deleteReview(customUserDetails, reviewId);
        return BaseResponse.success();
    }

    @Operation(summary = "식단(변동 메뉴) 리뷰 정보 조회(메뉴명, 평점 등등)", description = """
        식단 리뷰 정보를 조회하는 API 입니다.<br><br>
        메뉴명 리스트, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 각 평점의 개수를 조회합니다.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공", content = @Content(schema =
        @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema =
        @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema =
        @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/meals/{mealId}")
    public BaseResponse<MealReviewsResponse> getMealReviews(
        @Parameter(description = "mealId")
        @RequestParam(value = "mealId") Long mealId) {
        return BaseResponse.success(reviewService.findMealReviews(mealId));
    }

    @Operation(summary = "고정 메뉴 리뷰 정보 조회(메뉴명, 평점 등등)", description = """
        고정 메뉴 리뷰 정보를 조회하는 API 입니다.<br><br>
        메뉴명, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 각 평점의 개수를 조회합니다.
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공", content = @Content(schema =
        @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema =
        @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema =
        @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/menus/{menuId}")
    public BaseResponse<MainReviewsResponse> getMainReviews(
        @Parameter(description = "menuId")
        @RequestParam(value = "menuId") Long menuId) {
        return BaseResponse.success(reviewService.findMenuReviews(menuId));
    }

}
