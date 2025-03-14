package ssu.eatssu.domain.admin.dto;

import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;

import java.time.LocalDateTime;

public record ReportLine(Long reportId, LocalDateTime date, String type, Long reviewId, String reviewText) {
	public ReportLine(Report report, Review review) {
		this(report.getId(), report.getCreatedDate(), report.getReportType().getDescription(),
			review.getId(), review.getContent());
	}
}