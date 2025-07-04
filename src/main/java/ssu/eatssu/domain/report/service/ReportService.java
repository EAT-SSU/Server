package ssu.eatssu.domain.report.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.report.dto.ReportCreateRequest;
import ssu.eatssu.domain.report.dto.ReportTypeList;
import ssu.eatssu.domain.report.entity.ReportStatus;
import ssu.eatssu.domain.report.repository.ReportRepository;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_REVIEW;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public Report reportReview(CustomUserDetails userDetails, ReportCreateRequest request) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));

        Review review = reviewRepository.findById(request.reviewId())
                                        .orElseThrow(() -> new BaseException(NOT_FOUND_REVIEW));

        Report report = Report.create(user, review, request, ReportStatus.PENDING);
        return reportRepository.save(report);
    }

    public ReportTypeList getReportType() {
        return ReportTypeList.get();
    }
}
