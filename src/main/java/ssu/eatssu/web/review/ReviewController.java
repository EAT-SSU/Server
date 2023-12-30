package ssu.eatssu.web.review;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
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
import static ssu.eatssu.response.BaseResponseStatus.MISSING_QUERY_PARAM;

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
    @Operation(summary = "리뷰 작성", description = "리뷰 작성")
    @PostMapping(value = "/{menuId}",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public BaseResponse<String> writeReview(@Parameter(description = "menuId") @PathVariable("menuId") Long menuId,
                                            @RequestPart(value = "reviewCreate") ReviewCreate reviewCreate,
                                            @RequestPart(value = "multipartFileList", required = false)
                                                    List<MultipartFile> multipartFileList) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.createReview(userId, menuId, reviewCreate, multipartFileList);
        return new BaseResponse<>("");
    }

    /**
     * 리뷰 수정(글 수정)
     * <p>리뷰식별자(reviewId)에 해당하는 리뷰 속 글을 수정한다. 사진은 수정 X</p>
     */
    @Operation(summary = "리뷰 수정(글 수정)", description = "리뷰 수정(글 수정)")
    @PatchMapping("/{reviewId}")
    public BaseResponse<String> updateReview(@Parameter(description = "reviewId")
                                             @PathVariable("reviewId") Long reviewId,
                                             @RequestBody ReviewUpdate reviewUpdate) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.updateReview(userId, reviewId, reviewUpdate);
        return new BaseResponse<>("");
    }

    /**
     * 리뷰 삭제
     * <p>리뷰식별자(reviewId)에 해당하는 리뷰를 삭제한다.</p>
     */
    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제")
    @DeleteMapping("/{reviewId}")
    public BaseResponse<String> deleteReview(@Parameter(description = "reviewId") @PathVariable("reviewId") Long reviewId) {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.deleteReview(userId, reviewId);
        return new BaseResponse<>("");
    }

    /**
     * 리뷰 정보 조회
     * <p>식단(변동메뉴)리뷰 조회 시 <b>메뉴명 리스트</b>, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회한다.<br>
     * 고정메뉴 리뷰 조회 시 <b>메뉴명</b>, 리뷰 수, 메인 평점, 양 평점, 맛 평점, 평점 별 개수를 조회한다.</p>
     */
    @Operation(summary = "리뷰 정보 조회(평점 등등)", description = "리뷰 정보 조회(평점 등등)")
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
                throw new BaseException(MISSING_QUERY_PARAM);
            } else {
                menuReviewInfo = reviewService.findReviewInfoByMenuId(menuId);
            }
        } else if (menuTypeGroup == CHANGE) {
            if (mealId == null) {
                throw new BaseException(MISSING_QUERY_PARAM);
            } else {
                menuReviewInfo = reviewService.findReviewInfoByMealId(mealId);
            }
        } else {
            throw new BaseException(MISSING_QUERY_PARAM);
        }
        return new BaseResponse<>(menuReviewInfo);
    }

    /**
     * 리뷰 리스트 조회
     * <p>커서 기반 페이지네이션으로 리뷰 리스트를 조회한다.<br>
     * pageable default={size=20, sort=date, direction=desc}</p>
     */
    @Operation(summary = "리뷰 리스트 조회", description = "리뷰 리스트 조회")
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
                throw new BaseException(MISSING_QUERY_PARAM);
            } else {
                reviewList = reviewService.findReviewListByMenuId(menuId, pageable, lastReviewId);
            }
        } else if (menuTypeGroup == CHANGE) {
            if (mealId == null) {
                throw new BaseException(MISSING_QUERY_PARAM);
            } else {
                reviewList = reviewService.findReviewListByMealId(mealId, pageable, lastReviewId);
            }
        } else {
            throw new BaseException(MISSING_QUERY_PARAM);
        }
        return new BaseResponse<>(reviewList);
    }

    /*
    review 갯수, 별점 refresh
    //todo 관리자 api로 이동 필요
     */
    @GetMapping("/refresh")
    public BaseResponse<String> refreshReviewInfo() {
        refreshingService.refreshAllReviews();
        return new BaseResponse<>("");
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
