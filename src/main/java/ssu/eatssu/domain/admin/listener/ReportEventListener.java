package ssu.eatssu.domain.admin.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ssu.eatssu.domain.admin.event.ReviewDeleteEvent;
import ssu.eatssu.domain.admin.service.ManageReportService;

@Component
@RequiredArgsConstructor
public class ReportEventListener {

	private final ManageReportService manageReportService;

	@EventListener
	public void deleteReport(ReviewDeleteEvent event) {
		manageReportService.deleteAllByReviewId(event.reviewId());
	}
}
