package ssu.eatssu.domain.report.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.report.dto.request.ReportCreateRequest;
import ssu.eatssu.domain.report.dto.response.ReportTypeList;
import ssu.eatssu.domain.report.presentation.docs.ReportControllerDocs;
import ssu.eatssu.domain.report.service.ReportService;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
public class ReportController implements ReportControllerDocs {

    private final ReportService reportService;
    private final SlackService slackService;

    @Override
    @GetMapping("/types")
    public BaseResponse<ReportTypeList> getReportType() {
        ReportTypeList reportTypeList = reportService.getReportType();
        return BaseResponse.success(reportTypeList);
    }

    /**
     * 리뷰 신고
     */
    @Override
    @PostMapping("")
    public BaseResponse<Void> reportReview(@RequestBody ReportCreateRequest createReportRequest,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Report report = reportService.reportReview(customUserDetails, createReportRequest);
        slackService.sendSlackMessage(SlackMessageFormat.sendReport(report),
                                      SlackChannel.REPORT_CHANNEL);
        return BaseResponse.success();
    }

}
