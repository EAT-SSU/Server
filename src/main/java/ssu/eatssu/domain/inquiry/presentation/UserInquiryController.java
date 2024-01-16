package ssu.eatssu.domain.inquiry.presentation;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER_INQUIRY;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.user.entity.UserInquiry;
import ssu.eatssu.domain.repository.UserInquiryRepository;
import ssu.eatssu.domain.inquiry.service.UserInquiryService;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponse;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.entity.SlackService;
import ssu.eatssu.domain.slack.entity.SlackMessageFormat;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.domain.inquiry.dto.UserInquiryCreate;
import ssu.eatssu.domain.inquiry.dto.UserInquiryDetail;

@Slf4j
@RestController
@RequestMapping("/inquiry")
@RequiredArgsConstructor
@Tag(name = "Inquiry", description = "문의 API")
public class UserInquiryController {
    private final SlackService slackService;
    private final UserInquiryRepository userInquiryRepository;
    private final UserInquiryService userInquiryService;

    /**
     * 문의 작성
     * <p>문의를 작성합니다.</p>
     */
    @Operation(summary = "문의 작성", description = "문의를 작성하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 작성 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/")
    public BaseResponse<?> writeInquiry(@RequestBody UserInquiryCreate userInquiryCreate) {
        Long userId = SecurityUtil.getLoginUserId();
        UserInquiry inquiry = userInquiryService.createUserInquiry(userId, userInquiryCreate.getContent());
        slackService.sendSlackMessage(SlackMessageFormat.sendUserInquiry(inquiry)
                , SlackChannel.USER_INQUIRY_CHANNEL);
        return BaseResponse.success();
    }

    /**
     * 문의 내용 조회
     * <p>문의 식별자(userInquiryId)에 해당하는 문의 내용을 조회합니다.</p>
     */
    @Operation(summary = "문의 내용 조회", description = "문의 내용을 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문의 작성 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 문의", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/{userInquiryId}")
    public BaseResponse<UserInquiryDetail> getInquiryDetail(@Parameter(description = "userInquiryId")
                                                                @PathVariable("userInquiryId") Long userInquiryId) {
        UserInquiry inquiry =
                userInquiryRepository.findById(userInquiryId).orElseThrow(() -> new BaseException(NOT_FOUND_USER_INQUIRY));
        slackService.sendSlackMessage(SlackMessageFormat.sendUserInquiry(inquiry)
                , SlackChannel.USER_INQUIRY_CHANNEL);
        return BaseResponse.success(UserInquiryDetail.fromUserInquiry(inquiry));
    }

}
