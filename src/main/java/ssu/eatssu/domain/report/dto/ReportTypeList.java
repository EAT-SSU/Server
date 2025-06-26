package ssu.eatssu.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import ssu.eatssu.domain.report.entity.ReportType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Schema(title = "리뷰 신고 사유 목록 Response")
public class ReportTypeList {

    @Schema(description = "리뷰 신고 사유 목록")
    private final List<ReportTypeInformation> response = new ArrayList<>();

    public static ReportTypeList get() {
        ReportTypeList reportTypeList = new ReportTypeList();
        Arrays.stream(ReportType.values())
              .forEach(reportType -> reportTypeList.response.add(
                      new ReportTypeInformation(reportType.name(), reportType.getDescription())));
        return reportTypeList;
    }
}
