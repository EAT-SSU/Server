package ssu.eatssu.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(title = "리뷰 신고 사유")
public class ReportTypeInformation {
    @Schema(description = "신고 사유 타입")
    private final String type;

    @Schema(description = "신고 사유 설명")
    private final String description;

    public ReportTypeInformation(String type, String description) {
        this.type = type;
        this.description = description;
    }
}
