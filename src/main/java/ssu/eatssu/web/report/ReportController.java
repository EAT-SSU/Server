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
        ReviewReport report = reportService.reportReview(userId, reviewReportCreate);
        User reporter = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        Review review = reviewRepository.findById(reviewReportCreate.getReviewId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));
        slackService.sendSlackMessage(String.format(
                        """
                                ===================
                                *신고자 INFO*
                                - 신고자 ID: %d
                                - 닉네임: %s
                                *신고된 리뷰 INFO*
                                - 리뷰 ID: %d
                                - 리뷰 작성자 ID : %d
                                - 리뷰 작성자 닉네임 : %s
                                - 리뷰 메뉴: %s
                                - 리뷰 내용: %s
                                - 리뷰 날짜: %s
                                *신고 INFO*
                                - 신고사유: %s
                                - 신고 날짜: %s
                                ===================
                                """
                        , reporter.getId(), reporter.getNickname()
                        , review.getId(), review.getUser().getId(),review.getUser().getNickname(), review.getMenu().getName(),
                        review.getContent(),
                        review.getModifiedDate().toString()
                        ,report.getReportType().getKrName(), report.getCreatedDate()),
                SlackChannel.REPORT_CHANNEL);
        return ResponseEntity.ok("Success");
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
