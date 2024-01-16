package ssu.eatssu.domain.report.service;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.entity.CustomUserDetails;
import ssu.eatssu.domain.report.dto.ReportTypeResponse;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.report.entity.ReportStatus;
import ssu.eatssu.domain.review.repository.ReviewReportRepository;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.domain.report.dto.CreateReportRequest;
import ssu.eatssu.global.handler.response.BaseException;


@RequiredArgsConstructor
@Service
@Transactional
public class ReportService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewReportRepository reviewReportRepository;

    public Report report(CustomUserDetails userDetails, CreateReportRequest reviewReportCreate) {
        User user = userRepository.findById(userDetails.getId())
            .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(reviewReportCreate.getReviewId())
            .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        Report report = Report.create(user, review, reviewReportCreate, ReportStatus.PENDING);
        return reviewReportRepository.save(report);
    }

    public ReportTypeResponse getReportType() {
        return ReportTypeResponse.get();
    }
}
