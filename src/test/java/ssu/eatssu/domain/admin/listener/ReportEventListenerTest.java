package ssu.eatssu.domain.admin.listener;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.admin.event.ReviewDeleteEvent;
import ssu.eatssu.domain.admin.service.ManageReportService;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReportEventListenerTest {

    @Test
    void deleteReportDelegatesToManageReportService() {
        ManageReportService manageReportService = mock(ManageReportService.class);
        ReportEventListener listener = new ReportEventListener(manageReportService);

        listener.deleteReport(new ReviewDeleteEvent(1L));

        verify(manageReportService).deleteAllByReviewId(eq(1L));
    }
}
