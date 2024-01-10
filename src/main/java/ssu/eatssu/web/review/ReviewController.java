package ssu.eatssu.web.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.domain.menu.MenuType;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponse;
import ssu.eatssu.service.ReviewService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.review.dto.*;

import java.util.List;

import static ssu.eatssu.handler.response.BaseResponseStatus.MISSING_REQUEST_PARAM;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     * <p>메뉴식별자(menuId)에 해당하는 메뉴에 리뷰를 작성한다. 사진은 여러장 첨부 가능하다.</p>
     */
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
        @RequestPart(value = "reviewCreate") CreateReviewRequest request,
        @RequestPart(value = "multipartFileList", required = false)
        List<MultipartFile> multipartFileList) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.write(userId, menuId, request, multipartFileList);
        return BaseResponse.success();
    }

    /**
     * 리뷰 수정(글 수정)
     * <p>리뷰식별자(reviewId)에 해당하는 리뷰 속 글을 수정한다. 사진은 수정 X</p>
     */
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
                                             @RequestBody UpdateReviewRequest request) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.update(userId, reviewId, request);
        return BaseResponse.success();
    }

    /**
     * 리뷰 삭제
     * <p>리뷰식별자(reviewId)에 해당하는 리뷰를 삭제한다.</p>
     */
    @Operation(summary = "리뷰 삭제", description = "리뷰를 삭제하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 삭제 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "403", description = "리뷰에 대한 권한이 없음", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/{reviewId}")
    public BaseResponse<?> deleteReview(@Parameter(description = "reviewId") @PathVariable("reviewId") Long reviewId) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.delete(userId, reviewId);
        return BaseResponse.success();
    }

    /**
     * 식단(변동 메뉴) 리뷰 정보 조회
     * <p>메뉴명 리스트, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회한다.</p>
     */
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
    @GetMapping("/meal-info")
    public BaseResponse<MealReviewInformationResponse> getMealReviewInfo(@Parameter(description = "mealId")
                                                          @RequestParam(value = "mealId") Long mealId) {
        return BaseResponse.success(reviewService.findReviewInformationByMealId(mealId));
    }

    /**
     * 고정 메뉴 리뷰 정보 조회
     * <p>메뉴명, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회한다.</p>
     */
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
    @GetMapping("/menu-info")
    public BaseResponse<MenuReviewInformationResponse> getMenuReviewInfo(@Parameter(description = "menuId")
                                                          @RequestParam(value = "menuId") Long menuId) {
        return BaseResponse.success(reviewService.findReviewInformationByMenuId(menuId));
    }

    /**
     * 리뷰 목록 조회
     * <p>커서 기반 페이지네이션으로 리뷰 목록을 조회한다.<br>
     * pageable default={size=20, sort=date, direction=desc}</p>
     */
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
    @GetMapping("/list")
    public BaseResponse<SliceDto<ReviewDetail>> getReviewList(
        @Parameter(description = "타입(변동메뉴(식단)/고정메뉴)") @RequestParam("menuType") MenuType menuType,
        @Parameter(description = "menuId(고정메뉴)") @RequestParam(value = "menuId", required = false) Long menuId,
        @Parameter(description = "mealId(변동메뉴)") @RequestParam(value = "mealId", required = false) Long mealId,
        @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY)
        @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
        @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceDto<ReviewDetail> reviewList;
        if (menuType == MenuType.FIXED) {
            if (menuId == null) {
	throw new BaseException(MISSING_REQUEST_PARAM);
            } else {
	reviewList = reviewService.findReviewListByMenuId(menuId, pageable, lastReviewId);
            }
        } else if (menuType == MenuType.CHANGED) {
            if (mealId == null) {
	throw new BaseException(MISSING_REQUEST_PARAM);
            } else {
	reviewList = reviewService.findReviewListByMealId(mealId, pageable, lastReviewId);
            }
        } else {
            throw new BaseException(MISSING_REQUEST_PARAM);
        }
        return BaseResponse.success(reviewList);
    }

}
