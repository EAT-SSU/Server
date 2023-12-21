package ssu.eatssu.web.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.Review;
import ssu.eatssu.domain.ReviewReport;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.ReviewReportType;
import ssu.eatssu.domain.repository.ReviewRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.ReportService;
import ssu.eatssu.slack.SlackChannel;
import ssu.eatssu.slack.SlackMessageFormat;
import ssu.eatssu.slack.SlackService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.report.dto.ReviewReportCreate;
import ssu.eatssu.web.report.dto.ReviewReportTypeInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ssu.eatssu.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.response.BaseResponseStatus.NOT_FOUND_USER;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "Report", description = "신고 API")
public class ReportController {
    private final ReportService reportService;
    private final SlackService slackService;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    /**
     * 리뷰 신고 사유 받아오기
     */
    @Operation(summary = "리뷰 신고 사유 받아오기", description = "리뷰 신고 사유 받아오기")
    @GetMapping("/type")
    public BaseResponse<List<ReviewReportTypeInfo>> reportReviewType() {
        List<ReviewReportTypeInfo> reportInfo = new ArrayList<>();
        Arrays.stream(ReviewReportType.values())
                .forEach(reportType -> reportInfo.add(new ReviewReportTypeInfo(reportType)));
        return new BaseResponse<>(reportInfo);
    }

    /**
     * 리뷰 신고하기
     */
    @Operation(summary = "리뷰 신고하기", description = "리뷰 신고하기")
    @PostMapping("/")
    public BaseResponse<String> reportReview(@RequestBody ReviewReportCreate reviewReportCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        ReviewReport report = reportService.reportReview(userId, reviewReportCreate);
        slackService.sendSlackMessage(SlackMessageFormat.sendReport(report), SlackChannel.REPORT_CHANNEL);
        return new BaseResponse<>("Success");
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
