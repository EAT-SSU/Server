package ssu.eatssu.domain.report.entity;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReportTypeTest {

    @Test
    void fromReturnsMatchingTypeCaseInsensitively() {
        assertThat(ReportType.from("no_associate_content")).isEqualTo(ReportType.NO_ASSOCIATE_CONTENT);
        assertThat(ReportType.from("EXTRA")).isEqualTo(ReportType.EXTRA);
    }

    @Test
    void fromThrowsForUnknownName() {
        assertThatThrownBy(() -> ReportType.from("존재하지않음"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
