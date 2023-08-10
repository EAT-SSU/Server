package ssu.eatssu.web.mypage;

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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.MyPageService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.mypage.dto.MyReviewDetail;
import ssu.eatssu.web.mypage.dto.MypageInfo;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Tag(name = "MyPage", description = "마이페이지 API")
public class MyPageController {
    private final MyPageService myPageService;

    /**
     * 내가 쓴 리뷰 리스트
     */
    @Operation(summary = "내가 쓴 리뷰 모아보기", description = "내가 쓴 리뷰 리스트")
    @PostMapping("/myreview")
    public ResponseEntity<SliceDto<MyReviewDetail>> myReviewList(
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)",
                    in = ParameterIn.QUERY) @RequestParam(required = false) Long lastReviewId,
            @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Long userId = SecurityUtil.getLoginUserId();
        SliceDto<MyReviewDetail> myReviewList = myPageService.findMyReviewList(userId, pageable, lastReviewId);
        return ResponseEntity.ok(myReviewList);
    }

    /**
     * 마이페이지 정보
     */
    @Operation(summary = "마이페이지 정보", description = "마이페이지 정보")
    @PostMapping("/info")
    public ResponseEntity<MypageInfo> mypageInfo() {
        Long userId = SecurityUtil.getLoginUserId();
        MypageInfo mypageInfo = myPageService.mypageInfo(userId);
        return ResponseEntity.ok(mypageInfo);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
