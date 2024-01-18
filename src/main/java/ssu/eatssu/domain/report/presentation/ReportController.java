package ssu.eatssu.domain.report.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.report.service.ReportService;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.domain.report.dto.CreateReportRequest;
import ssu.eatssu.domain.report.dto.ReportTypeResponse;

import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reports")
@Tag(name = "Report", description = "신고 API")
public class ReportController {

    private final ReportService reportService;
    private final SlackService slackService;

    @Operation(summary = "리뷰 신고 사유 종류 조회", description = "리뷰 신고 사유 종류를 조회하는 API 입니다.")
    @GetMapping("/types")
    public BaseResponse<ReportTypeResponse> getReportType() {
        return BaseResponse.success(reportService.getReportType());
    }

    /**
     * 리뷰 신고
     */
    @Operation(summary = "리뷰 신고하기", description = "리뷰를 신고하는 API 입니다.")
    @PostMapping("")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "리뷰 신고 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<Void> reportReview(@RequestBody CreateReportRequest createReportRequest,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Report report = reportService.report(customUserDetails, createReportRequest);
        slackService.sendSlackMessage(SlackMessageFormat.sendReport(report),
            SlackChannel.REPORT_CHANNEL);
        return BaseResponse.success();
    }

}
