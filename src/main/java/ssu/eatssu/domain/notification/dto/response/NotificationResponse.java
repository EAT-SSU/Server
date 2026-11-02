package ssu.eatssu.domain.notification.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.entity.NotificationType;
import ssu.eatssu.domain.notification.service.NotificationText;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Schema(title = "알림")
public record NotificationResponse(
        @Schema(description = "알림 id", example = "1")
        Long notificationId,
        @Schema(description = "알림 종류", example = "MENU_FAVORITE")
        NotificationType type,
        @Schema(description = "제목(사용자 현재 언어)", example = "🍽️ 찜한 메뉴가 나온대요!")
        String title,
        @Schema(description = "본문(사용자 현재 언어)", example = "오늘 점심, 학생식당에 돈까스 등장 👀")
        String body,
        @Schema(description = "이동할 식단 날짜(yyyyMMdd)", example = "20261102")
        String date,
        @Schema(description = "이동할 시간대", example = "LUNCH")
        TimePart timePart,
        @Schema(description = "읽음 여부", example = "false")
        boolean isRead,
        @Schema(description = "알림 생성 시각", example = "2026-11-02T11:00:00")
        LocalDateTime createdAt
) {

    public static NotificationResponse of(Notification notification, NotificationText text, TimePart timePart) {
        return new NotificationResponse(notification.getId(), notification.getType(), text.title(), text.body(),
                                        notification.getTargetDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                                        timePart, notification.isRead(), notification.getCreatedDate());
    }
}
