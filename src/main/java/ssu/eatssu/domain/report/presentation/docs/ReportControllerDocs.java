package ssu.eatssu.domain.report.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.report.dto.ReportCreateRequest;
import ssu.eatssu.domain.report.dto.ReportTypeList;
import ssu.eatssu.global.handler.response.BaseResponse;

@Tag(name = "Report", description = "신고 API")
public interface ReportControllerDocs {

    @Operation(summary = "리뷰 신고 사유 종류 조회", description = "리뷰 신고 사유 종류를 조회하는 API 입니다.")
    BaseResponse<ReportTypeList> getReportType();

    @Operation(summary = "리뷰 신고하기", description = "리뷰를 신고하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "리뷰 신고 성공", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 리뷰", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<Void> reportReview(ReportCreateRequest createReportRequest, CustomUserDetails customUserDetails);
}
