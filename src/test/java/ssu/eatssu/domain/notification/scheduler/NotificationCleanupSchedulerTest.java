package ssu.eatssu.domain.notification.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import ssu.eatssu.domain.notification.service.NotificationService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationCleanupSchedulerTest {

    @Test
    void deletesExpiredNotificationsEveryDayAtFourInSeoul() throws Exception {
        // given
        NotificationService service = mock(NotificationService.class);
        NotificationCleanupScheduler scheduler = new NotificationCleanupScheduler(service);
        Scheduled scheduled = NotificationCleanupScheduler.class.getMethod("deleteExpiredNotifications")
                                                                .getAnnotation(Scheduled.class);

        // when
        scheduler.deleteExpiredNotifications();

        // then
        verify(service).deleteExpiredNotifications();
        assertThat(scheduled.cron()).isEqualTo("0 0 4 * * *");
        assertThat(scheduled.zone()).isEqualTo("Asia/Seoul");
    }
}
