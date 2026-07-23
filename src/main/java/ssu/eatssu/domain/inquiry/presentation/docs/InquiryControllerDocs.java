package ssu.eatssu.domain.inquiry.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.inquiry.dto.CreateInquiryRequest;
import ssu.eatssu.global.handler.response.BaseResponse;

@Tag(name = "Inquiry", description = "문의 API")
public interface InquiryControllerDocs {

    @Operation(summary = "문의 작성", description = "문의를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 작성 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<Void> writeInquiry(CreateInquiryRequest createInquiryRequest,
                                    CustomUserDetails customUserDetails);
}
