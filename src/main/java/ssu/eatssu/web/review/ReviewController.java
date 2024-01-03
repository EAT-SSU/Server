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
import ssu.eatssu.domain.enums.MenuTypeGroup;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponse;
import ssu.eatssu.service.RefreshingService;
import ssu.eatssu.service.ReviewService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.review.dto.MenuReviewInfo;
import ssu.eatssu.web.review.dto.ReviewCreate;
import ssu.eatssu.web.review.dto.ReviewDetail;
import ssu.eatssu.web.review.dto.ReviewUpdate;

import java.util.List;

import static ssu.eatssu.domain.enums.MenuTypeGroup.CHANGE;
import static ssu.eatssu.domain.enums.MenuTypeGroup.FIX;
import static ssu.eatssu.handler.response.BaseResponseStatus.MISSING_REQUEST_PARAM;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name = "Review", description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;
    private final RefreshingService refreshingService;

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
    public BaseResponse<?> writeReview(@Parameter(description = "menuId") @PathVariable("menuId") Long menuId,
                                            @RequestPart(value = "reviewCreate") ReviewCreate reviewCreate,
                                            @RequestPart(value = "multipartFileList", required = false)
                                                    List<MultipartFile> multipartFileList) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.createReview(userId, menuId, reviewCreate, multipartFileList);
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
                                             @RequestBody ReviewUpdate reviewUpdate) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.updateReviewContent(userId, reviewId, reviewUpdate);
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
        reviewService.deleteReview(userId, reviewId);
        return BaseResponse.success();
    }

    /**
     * 리뷰 정보 조회
     * <p>식단(변동메뉴)리뷰 조회 시 <b>메뉴명 리스트</b>, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회한다.<br>
     * 고정메뉴 리뷰 조회 시 <b>메뉴명</b>, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회한다.</p>
     */
    @Operation(summary = "리뷰 정보 조회(평점 등등)", description = """
        리뷰 정보를 조회하는 API 입니다.<br><br>
        menuType=FIX 의 경우 menuId 파라미터를 넣어주세요.<br><br>
        고정메뉴 리뷰 조회 시 <b>메뉴명</b>, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회합니다.
        menuType=CHANGE 의 경우 mealId 파라미터를 넣어주세요.<br><br>
        식단(변동메뉴)리뷰 조회 시 <b>메뉴명 리스트</b>, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회합니다.<br><br>
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 정보 조회 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "쿼리 파라미터 누락", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 메뉴", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 식단", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/info")
    public BaseResponse<MenuReviewInfo> getMenuReviewInfo(@Parameter(description = "타입(변동메뉴(식단)/고정메뉴)")
                                                          @RequestParam("menuType") MenuTypeGroup menuTypeGroup,
                                                          @Parameter(description = "menuId(고정메뉴)")
                                                          @RequestParam(value = "menuId", required = false) Long menuId,
                                                          @Parameter(description = "mealId(고정메뉴)")
                                                          @RequestParam(value = "mealId", required = false) Long mealId) {
        MenuReviewInfo menuReviewInfo;
        if (menuTypeGroup == FIX) {
            if (menuId == null) {
                throw new BaseException(MISSING_REQUEST_PARAM);
            } else {
                menuReviewInfo = reviewService.findReviewInfoByMenuId(menuId);
            }
        } else if (menuTypeGroup == CHANGE) {
            if (mealId == null) {
                throw new BaseException(MISSING_REQUEST_PARAM);
            } else {
                menuReviewInfo = reviewService.findReviewInfoByMealId(mealId);
            }
        } else {
            throw new BaseException(MISSING_REQUEST_PARAM);
        }
        return BaseResponse.success(menuReviewInfo);
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
            @Parameter(description = "타입(변동메뉴(식단)/고정메뉴)") @RequestParam("menuType") MenuTypeGroup menuTypeGroup,
            @Parameter(description = "menuId(고정메뉴)") @RequestParam(value = "menuId", required = false) Long menuId,
            @Parameter(description = "mealId(변동메뉴)") @RequestParam(value = "mealId", required = false) Long mealId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY)
            @RequestParam(value = "lastReviewId", required = false) Long lastReviewId,
            @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        SliceDto<ReviewDetail> reviewList;
        if (menuTypeGroup == FIX) {
            if (menuId == null) {
                throw new BaseException(MISSING_REQUEST_PARAM);
            } else {
                reviewList = reviewService.findReviewListByMenuId(menuId, pageable, lastReviewId);
            }
        } else if (menuTypeGroup == CHANGE) {
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

    /*
    review 갯수, 별점 refresh
    //todo 관리자 api로 이동 필요
     */
    @GetMapping("/refresh")
    public BaseResponse<?> refreshReviewInfo() {
        refreshingService.refreshAllReviews();
        return BaseResponse.success();
    }

}
