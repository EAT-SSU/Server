package ssu.eatssu.domain.report.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportStatusTest {

    @Test
    void fromReturnsMatchingStatusByDescription() {
        assertThat(ReportStatus.from("대기 중")).isEqualTo(ReportStatus.PENDING);
        assertThat(ReportStatus.from("해결됨")).isEqualTo(ReportStatus.RESOLVED);
        assertThat(ReportStatus.PENDING.getDescription()).isEqualTo("대기 중");
    }

    @Test
    void fromThrowsForUnknownDescription() {
        assertThatThrownBy(() -> ReportStatus.from("존재하지않음"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
