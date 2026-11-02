package ssu.eatssu.domain.notification.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.response.NotificationResponse;
import ssu.eatssu.domain.notification.dto.response.UnreadNotificationResponse;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@Tag(name = "Notification", description = "알림함 API")
public interface NotificationControllerDocs {

    @Operation(summary = "알림 목록 조회", description = """
            최근 7일(오늘 포함, 식단 날짜 기준) 찜 메뉴 알림을 최신순으로 조회하는 API 입니다.<br><br>
            제목/본문은 조회 시점의 사용자 언어(User.language)로 만들어집니다. 언어를 바꾸면 지난 알림 문구도 바뀝니다.<br>
            오늘 알림은 '오늘 점심', 지난 알림은 '11월 2일 점심'처럼 표시됩니다.<br>
            알림을 누르면 date(yyyyMMdd)와 timePart에 해당하는 학식 탭으로 이동합니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<List<NotificationResponse>> getNotifications(CustomUserDetails userDetails);

    @Operation(summary = "안 읽은 알림 여부 조회", description = "학식 탭 알림 아이콘의 점 표시 여부를 조회하는 API 입니다. 최근 7일 알림만 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "안 읽은 알림 여부 조회 성공"),
    })
    BaseResponse<UnreadNotificationResponse> hasUnread(CustomUserDetails userDetails);

    @Operation(summary = "알림 읽음 처리", description = "알림 하나를 읽음으로 바꾸는 API 입니다. 이미 읽은 알림이어도 성공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않거나 내 알림이 아님", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> read(Long notificationId, CustomUserDetails userDetails);

    @Operation(summary = "알림 모두 읽음 처리", description = "내 알림을 모두 읽음으로 바꾸는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 모두 읽음 처리 성공"),
    })
    BaseResponse<?> readAll(CustomUserDetails userDetails);
}
