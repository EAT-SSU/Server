package ssu.eatssu.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.domain.report.entity.ReportType;

@Schema(title = "리뷰 신고하기")
public record ReportCreateRequest(

        @Schema(description = "신고할 리뷰 id", example = "4")
        Long reviewId,

        @Schema(description = "신고 타입", example = "BAD_WORD")
        ReportType reportType,

        @Schema(description = "신고 내용", example = "음란성, 욕설 등 부적절한 내용")
        String content) {

}
