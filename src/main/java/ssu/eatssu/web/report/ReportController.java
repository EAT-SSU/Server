package ssu.eatssu.web.report;

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
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.enums.ReviewReportType;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.MyPageService;
import ssu.eatssu.service.ReportService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.SliceDto;
import ssu.eatssu.web.mypage.dto.MyReviewDetail;
import ssu.eatssu.web.mypage.dto.MypageInfo;
import ssu.eatssu.web.report.dto.ReviewReportCreate;
import ssu.eatssu.web.report.dto.ReviewReportTypeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "Report", description = "신고 API")
public class ReportController {
    private final ReportService reportService;

    /**
     * 리뷰 신고 사유 받아오기
     */
    @Operation(summary = "리뷰 신고 사유 받아오기", description = "리뷰 신고 사유 받아오기")
    @GetMapping("/type")
    public ResponseEntity<List<ReviewReportTypeInfo>> reportReviewType() {
        List<ReviewReportTypeInfo> reportInfo = new ArrayList<>();
        Arrays.stream(ReviewReportType.values())
                .forEach(reportType -> reportInfo.add(new ReviewReportTypeInfo(reportType)));
        return ResponseEntity.ok(reportInfo);
    }

    /**
     * 리뷰 신고하기
     */
    @Operation(summary = "리뷰 신고하기", description = "리뷰 신고하기")
    @PostMapping("/")
    public ResponseEntity<String> reportReview(@RequestBody ReviewReportCreate reviewReportCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        reportService.reportReview(userId, reviewReportCreate);
        return ResponseEntity.ok("Success");
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
