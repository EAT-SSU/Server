package ssu.eatssu.web.user_inquiries;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.UserInquiries;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.UserInquiriesService;
import ssu.eatssu.slack.SlackChannel;
import ssu.eatssu.slack.SlackService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.user_inquiries.dto.UserInquiriesCreate;

@Slf4j
@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
@Tag(name = "Inquiries", description = "문의 API")
public class UserInquiriesController {
    private final SlackService slackService;
    private final UserRepository userRepository;
    private final UserInquiriesService userInquiriesService;

    /**
     * 문의 남기기
     */
    @Operation(summary = "문의 남기기", description = "문의 남기기")
    @PostMapping("/")
    public BaseResponse<String> userInquiriesCreate(@RequestBody UserInquiriesCreate userInquiriesCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        UserInquiries inquiries = userInquiriesService.createUserInquiries(userId, userInquiriesCreate.getContent());
        slackService.sendSlackMessage(String.format(
                        """
                                ===================
                                *문의 INFO*
                                - 문의자 ID: %d
                                - 닉네임: %s
                                - 이메일: %s
                                *문의 내용*
                                - Date: %s
                                - Content: %s
                                ===================
                                """
                        , inquiries.getUser().getId(), inquiries.getUser().getNickname()
                ,inquiries.getUser().getEmail(),inquiries.getCreatedDate(), inquiries.getContent()),
                SlackChannel.USER_INQUIRIES_CHANNEL);
        return new BaseResponse<>("");
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
