package ssu.eatssu.domain.report.dto;

import lombok.Getter;
import ssu.eatssu.domain.report.entity.ReviewReportType;

@Getter
public class ReviewReportTypeInfo {
    private ReviewReportType reportType;
    private String typeDescription;

    public ReviewReportTypeInfo(ReviewReportType reportType) {
        this.reportType = reportType;
        this.typeDescription = reportType.getDescription();
    }
}
