package ssu.eatssu.domain.notification.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.notification.service.NotificationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanupScheduler {

    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
    public void deleteExpiredNotifications() {
        int deleted = notificationService.deleteExpiredNotifications();
        log.info("만료된 알림 정리 완료. deleted={}", deleted);
    }
}
