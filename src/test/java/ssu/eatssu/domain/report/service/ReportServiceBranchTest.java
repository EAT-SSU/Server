package ssu.eatssu.domain.report.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.report.dto.request.ReportCreateRequest;
import ssu.eatssu.domain.report.entity.ReportStatus;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.report.repository.ReportRepository;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.review.repository.ReviewRepository;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReportServiceBranchTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private UserRepository userRepository;
    @Mock private ReportRepository reportRepository;
    @Mock private ApplicationEventPublisher eventPublisher;

    @InjectMocks private ReportService reportService;

    @Test
    void reportReviewThrowsWhenUserDoesNotExist() {
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.reportReview(userDetails(), request()))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void reportReviewThrowsWhenReviewDoesNotExist() {
        User user = org.mockito.Mockito.mock(User.class);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reviewRepository.findById(2L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.reportReview(userDetails(), request()))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void reportReviewThrowsWhenRecentReportExists() {
        User user = user();
        Review review = review();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));
        given(reportRepository.existsRecentReport(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(2L),
                any(LocalDateTime.class))).willReturn(true);

        assertThatThrownBy(() -> reportService.reportReview(userDetails(), request()))
                .isInstanceOf(BaseException.class);
    }

    @Test
    void reportReviewSavesPendingReportAndPublishesEvent() {
        User user = user();
        Review review = review();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(reviewRepository.findById(2L)).willReturn(Optional.of(review));
        given(reportRepository.existsRecentReport(org.mockito.ArgumentMatchers.eq(1L), org.mockito.ArgumentMatchers.eq(2L),
                any(LocalDateTime.class))).willReturn(false);
        ArgumentCaptor<Report> reportCaptor = ArgumentCaptor.forClass(Report.class);

        Report report = reportService.reportReview(userDetails(), request());

        verify(reportRepository).save(reportCaptor.capture());
        verify(eventPublisher).publishEvent(any(Object.class));
        assertThat(report).isSameAs(reportCaptor.getValue());
        assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
    }

    private ReportCreateRequest request() {
        return new ReportCreateRequest(2L, ReportType.IMPROPER_ADVERTISEMENT, "광고입니다");
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }

    private User user() {
        User user = org.mockito.Mockito.mock(User.class);
        given(user.getId()).willReturn(1L);
        return user;
    }

    private Review review() {
        Review review = org.mockito.Mockito.mock(Review.class);
        given(review.getId()).willReturn(2L);
        return review;
    }
}
