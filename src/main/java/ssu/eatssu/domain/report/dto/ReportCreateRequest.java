package ssu.eatssu.domain.report.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.domain.report.entity.ReportType;

@Schema(title = "리뷰 신고하기")
public record ReportCreateRequest(

        @Schema(description = "신고할 리뷰 id", example = "4")
        Long reviewId,

        @Schema(description = "신고 타입", example = "NO_ASSOCIATE_CONTENT")
        ReportType reportType,

        @Schema(description = "신고 내용", example = "리뷰 작성 취지에 맞지 않는 내용")
        String content) {

}
