package ssu.eatssu.domain.report.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.review.entity.ReviewReport;
import ssu.eatssu.domain.report.entity.ReviewReportType;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.handler.response.BaseResponse;
import ssu.eatssu.domain.report.service.ReportService;
import ssu.eatssu.slack.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.slack.SlackService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.domain.report.dto.ReviewReportCreate;
import ssu.eatssu.domain.report.dto.ReviewReportTypeInfo;

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
    @Operation(summary = "리뷰 신고 사유 종류 조회", description = "리뷰 신고 사유 종류를 조회하는 API 입니다.")
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
    @Operation(summary = "리뷰 신고하기", description = "리뷰를 신고하는 API 입니다.")
    @PostMapping("/")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 신고 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    public BaseResponse<?> reportReview(@RequestBody ReviewReportCreate reviewReportCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        ReviewReport report = reportService.reportReview(userId, reviewReportCreate);
        slackService.sendSlackMessage(SlackMessageFormat.sendReport(report), SlackChannel.REPORT_CHANNEL);
        return BaseResponse.success();
    }

}