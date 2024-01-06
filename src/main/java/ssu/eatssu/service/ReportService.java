package ssu.eatssu.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.review.Review;
import ssu.eatssu.domain.ReviewReport;
import ssu.eatssu.domain.User;
import ssu.eatssu.domain.enums.ReportStatus;
import ssu.eatssu.domain.repository.ReviewReportRepository;
import ssu.eatssu.domain.repository.ReviewRepository;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.web.report.dto.ReviewReportCreate;

import static ssu.eatssu.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewReportRepository reviewReportRepository;

    /**
     * 리뷰 신고
     */
    public ReviewReport reportReview(Long userId, ReviewReportCreate reviewReportCreate) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        Review review = reviewRepository.findById(reviewReportCreate.getReviewId())
                .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));
        ReviewReport reviewReport = ReviewReport.builder()
                .review(review).user(user).reportType(reviewReportCreate.getReportType())
                .content(reviewReportCreate.getContent()).status(ReportStatus.PENDING)
                .build();
        return reviewReportRepository.save(reviewReport);
    }
}
