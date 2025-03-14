package ssu.eatssu.domain.inquiry.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.inquiry.dto.CreateInquiryRequest;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.service.InquiryService;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
@Tag(name = "Inquiry", description = "문의 API")
public class InquiryController {

	private final SlackService slackService;
	private final InquiryService inquiryService;

	@Operation(summary = "문의 작성", description = "문의를 작성하는 API 입니다.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "문의 작성 성공"),
		@ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
	})
	@PostMapping("/")
	public BaseResponse<Void> writeInquiry(@RequestBody CreateInquiryRequest createInquiryRequest,
		@AuthenticationPrincipal CustomUserDetails customUserDetails) {
		Inquiry inquiry = inquiryService.createUserInquiry(customUserDetails, createInquiryRequest);
		slackService.sendSlackMessage(SlackMessageFormat.sendUserInquiry(inquiry)
			, SlackChannel.USER_INQUIRY_CHANNEL);
		return BaseResponse.success();
	}

}
