package ssu.eatssu.domain.inquiry.presentation;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.inquiry.dto.CreateInquiryRequest;
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.presentation.docs.InquiryControllerDocs;
import ssu.eatssu.domain.inquiry.service.InquiryService;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.global.handler.response.BaseResponse;

/**
 * 문의하기는 카카오톡으로 이동되어 사용되지 않고 있습니다.
 */
@Deprecated
@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryController implements InquiryControllerDocs {

    private final SlackService slackService;
    private final InquiryService inquiryService;

    @Override
    @PostMapping("/")
    public BaseResponse<Void> writeInquiry(@RequestBody CreateInquiryRequest createInquiryRequest,
                                           @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Inquiry inquiry = inquiryService.createUserInquiry(customUserDetails, createInquiryRequest);
        slackService.sendSlackMessage(SlackMessageFormat.sendUserInquiry(inquiry)
                , SlackChannel.USER_INQUIRY_CHANNEL);
        return BaseResponse.success();
    }

}
