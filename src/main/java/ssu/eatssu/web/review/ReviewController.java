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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ssu.eatssu.service.ReviewService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.review.dto.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
@Tag(name="Review",description = "리뷰 API")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 작성
     */
    @Operation(summary = "리뷰 작성", description = "리뷰 작성")
    @PostMapping(value = "{menuId}/detail", consumes = {
            MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE}, produces =
            MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity reviewCreate(@Parameter(description = "menuId")@PathVariable("menuId") Long menuId,
                                       @RequestPart(value = "reviewCreate") ReviewCreate reviewCreate,
                                       @RequestPart(value= "multipartFileList", required = false) List<MultipartFile> multipartFileList
                                       ){

        Long userId = SecurityUtil.getLoginUserId();
        try {
            reviewService.createReview(userId, menuId, reviewCreate, multipartFileList);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 리뷰 수정(글 수정)
     */
    @Operation(summary = "리뷰 수정(글 수정)", description = "리뷰 수정(글 수정)")
    @PatchMapping("{menuId}/detail/{reviewId}")
    public ResponseEntity reviewUpdate(@Parameter(description = "menuId")@PathVariable("menuId") Long menuId,
                                       @Parameter(description = "reviewId")@PathVariable("reviewId") Long reviewId,
                                       @RequestBody ReviewUpdate reviewUpdate) throws IllegalAccessException {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.updateReview(userId,reviewId, reviewUpdate);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 리뷰 삭제
     */
    @Operation(summary = "리뷰 삭제", description = "리뷰 삭제")
    @DeleteMapping("{menuId}/detail/{reviewId}")
    public ResponseEntity reviewDelete(@Parameter(description = "menuId")@PathVariable("menuId") Long menuId,
                                       @Parameter(description = "reviewId")@PathVariable("reviewId") Long reviewId) throws IllegalAccessException {
        Long userId = SecurityUtil.getLoginUserId();
        reviewService.deleteReview(userId,reviewId);
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 메뉴 리뷰 정보 조회(평점 등등)
     */
    @Operation(summary = "메뉴 리뷰 정보 조회(평점 등등)", description = "메뉴 리뷰 정보 조회(평점 등등)")
    @GetMapping("{menuId}")
    public ResponseEntity<MenuReviewInfo> menuReviewInfo(
            @Parameter(description = "menuId")@PathVariable("menuId") Long menuId) throws IllegalAccessException {
        MenuReviewInfo menuReviewInfo = reviewService.findMenuReviewInfo(menuId);
        return ResponseEntity.ok(menuReviewInfo);
    }

    /**
     * 메뉴 리뷰 리스트 조회
     */
    @Operation(summary = "메뉴 리뷰 리스트 조회", description = "메뉴 리뷰 리스트 조회")
    @GetMapping("{menuId}/list")
    public ResponseEntity<SliceDto<ReviewDetail>> menuReviewInfo(
            @Parameter(description = "menuId")@PathVariable("menuId") Long menuId,
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)",
                    in = ParameterIn.QUERY)@RequestParam(required = false) Long lastReviewId,
            @ParameterObject @PageableDefault(size=20, sort="date",direction = Sort.Direction.DESC) Pageable pageable)
    {
        SliceDto<ReviewDetail> reviewList = reviewService.findReviewListByMenu(menuId, pageable, lastReviewId);
        return ResponseEntity.ok(reviewList);
    }






}
