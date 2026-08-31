package ssu.eatssu.domain.report.dto.response;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.report.entity.ReportType;

import static org.assertj.core.api.Assertions.assertThat;

class ReportTypeListTest {

    @Test
    void getIncludesEveryReportTypeWithDescription() {
        ReportTypeList response = ReportTypeList.get();

        assertThat(response.getResponse()).hasSize(ReportType.values().length);
        assertThat(response.getResponse()).extracting(ReportTypeInformation::getType)
                .contains(ReportType.IMPROPER_CONTENT.name());
        assertThat(response.getResponse()).allSatisfy(type -> assertThat(type.getDescription()).isNotBlank());
    }
}
