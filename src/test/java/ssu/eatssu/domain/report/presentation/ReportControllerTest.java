package ssu.eatssu.domain.report.presentation;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.report.dto.response.ReportTypeList;
import ssu.eatssu.domain.report.service.ReportService;
import ssu.eatssu.domain.slack.service.SlackService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReportControllerTest {

    @Test
    void 신고_유형을_반환한다() {
        ReportService reportService = mock(ReportService.class);
        ReportTypeList types = ReportTypeList.get();
        when(reportService.getReportType()).thenReturn(types);
        ReportController controller = new ReportController(reportService, mock(SlackService.class));

        assertThat(controller.getReportType().getResult()).isSameAs(types);
    }
}
