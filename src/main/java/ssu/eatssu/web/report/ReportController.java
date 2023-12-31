package ssu.eatssu.web.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.ReviewReport;
import ssu.eatssu.domain.enums.ReviewReportType;
import ssu.eatssu.domain.repository.ReviewRepository;
import ssu.eatssu.domain.repository.UserRepository;
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
     * 리뷰 신고 사유 종류 조회
     */
    @Operation(summary = "리뷰 신고 사유 받아오기", description = "리뷰 신고 사유 받아오기")
    @GetMapping("/type")
    public BaseResponse<List<ReviewReportTypeInfo>> getReportType() {
        List<ReviewReportTypeInfo> reportInfo = new ArrayList<>();
        Arrays.stream(ReviewReportType.values())
                .forEach(reportType -> reportInfo.add(new ReviewReportTypeInfo(reportType)));
        return BaseResponse.success(reportInfo);
    }

    /**
     * 리뷰 신고
     */
    @Operation(summary = "리뷰 신고하기", description = "리뷰 신고하기")
    @PostMapping("/")
    public BaseResponse reportReview(@RequestBody ReviewReportCreate reviewReportCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        ReviewReport report = reportService.reportReview(userId, reviewReportCreate);
        slackService.sendSlackMessage(SlackMessageFormat.sendReport(report), SlackChannel.REPORT_CHANNEL);
        return BaseResponse.success();
    }

}
