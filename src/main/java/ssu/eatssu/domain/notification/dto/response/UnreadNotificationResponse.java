package ssu.eatssu.domain.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(title = "안 읽은 알림 여부")
public record UnreadNotificationResponse(
        @Schema(description = "최근 7일 안에 안 읽은 알림이 있는지", example = "true")
        boolean hasUnread
) {

}
