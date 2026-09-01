package ssu.eatssu.domain.review.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.report.dto.request.ReportCreateRequest;
import ssu.eatssu.domain.report.entity.ReportStatus;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.user.entity.User;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ReportEntityTest {

    @Test
    void createsReportFromRequestWithStatus() {
        User user = mock(User.class);
        Review review = Review.builder().build();
        ReportCreateRequest request = new ReportCreateRequest(3L, ReportType.NO_ASSOCIATE_CONTENT, "내용 불일치");

        Report report = Report.create(user, review, request, ReportStatus.PENDING);

        assertThat(report.getUser()).isSameAs(user);
        assertThat(report.getReview()).isSameAs(review);
        assertThat(report.getReportType()).isEqualTo(ReportType.NO_ASSOCIATE_CONTENT);
        assertThat(report.getContent()).isEqualTo("내용 불일치");
        assertThat(report.getStatus()).isEqualTo(ReportStatus.PENDING);
        assertThat(report.getId()).isNull();
    }
}
