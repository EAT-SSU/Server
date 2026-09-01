package ssu.eatssu.domain.report.dto.request;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.report.entity.ReportType;

import static org.assertj.core.api.Assertions.assertThat;

class ReportCreateRequestTest {

    @Test
    void exposesFieldsAndSupportsValueEquality() {
        ReportCreateRequest request = new ReportCreateRequest(1L, ReportType.EXTRA, "신고내용");
        ReportCreateRequest same = new ReportCreateRequest(1L, ReportType.EXTRA, "신고내용");

        assertThat(request.reviewId()).isEqualTo(1L);
        assertThat(request.reportType()).isEqualTo(ReportType.EXTRA);
        assertThat(request.content()).isEqualTo("신고내용");
        assertThat(request).isEqualTo(same).hasSameHashCodeAs(same);
        assertThat(request.toString()).contains("신고내용");
    }
}
