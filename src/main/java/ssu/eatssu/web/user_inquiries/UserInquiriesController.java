package ssu.eatssu.web.user_inquiries;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.UserInquiries;
import ssu.eatssu.domain.repository.UserInquiriesRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.UserInquiriesService;
import ssu.eatssu.slack.SlackChannel;
import ssu.eatssu.slack.SlackMessageFormat;
import ssu.eatssu.slack.SlackService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.user_inquiries.dto.UserInquiriesCreate;
import ssu.eatssu.web.user_inquiries.dto.UserInquiriesDetail;

import static ssu.eatssu.response.BaseResponseStatus.NOT_FOUND_USER_INQUIRIES;

@Slf4j
@RestController
@RequestMapping("/inquiries")
@RequiredArgsConstructor
@Tag(name = "Inquiries", description = "문의 API")
public class UserInquiriesController {
    private final SlackService slackService;
    private final UserInquiriesRepository userInquiriesRepository;
    private final UserInquiriesService userInquiriesService;

    /**
     * 문의 작성
     * <p>문의를 작성합니다.</p>
     */
    @Operation(summary = "문의 남기기", description = "문의 남기기")
    @PostMapping("/")
    public BaseResponse<String> writeInquiries(@RequestBody UserInquiriesCreate userInquiriesCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        UserInquiries inquiries = userInquiriesService.createUserInquiries(userId, userInquiriesCreate.getContent());
        slackService.sendSlackMessage(SlackMessageFormat.sendUserInquiries(inquiries)
                , SlackChannel.USER_INQUIRIES_CHANNEL);
        return new BaseResponse<>("");
    }

    /**
     * 문의 내용 조회
     * <p>문의 식별자(userInquiriesId)에 해당하는 문의 내용을 조회합니다.</p>
     */
    @Operation(summary = "고객 문의 내역 조회", description = "고객 문의 내역 조회 By Id")
    @GetMapping("/{userInquiriesId}")
    public BaseResponse<UserInquiriesDetail> getInquiriesDetail(@Parameter(description = "userInquiriesId")
                                                                @PathVariable("userInquiriesId") Long userInquiriesId) {
        UserInquiries inquiries =
                userInquiriesRepository.findById(userInquiriesId).orElseThrow(() -> new BaseException(NOT_FOUND_USER_INQUIRIES));
        slackService.sendSlackMessage(SlackMessageFormat.sendUserInquiries(inquiries)
                , SlackChannel.USER_INQUIRIES_CHANNEL);
        return new BaseResponse<>(UserInquiriesDetail.fromUserInquiries(inquiries));
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }

}
