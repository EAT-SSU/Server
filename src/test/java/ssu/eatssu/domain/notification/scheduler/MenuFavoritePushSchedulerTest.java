package ssu.eatssu.domain.notification.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.annotation.Scheduled;
import ssu.eatssu.domain.notification.service.MenuFavoritePushService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class MenuFavoritePushSchedulerTest {

    @Test
    void runsTodayPushesEveryDayAtElevenInSeoul() throws Exception {
        // given
        MenuFavoritePushService service = mock(MenuFavoritePushService.class);
        MenuFavoritePushScheduler scheduler = new MenuFavoritePushScheduler(service);
        Scheduled scheduled = MenuFavoritePushScheduler.class.getMethod("sendMenuFavoritePushes")
                                                             .getAnnotation(Scheduled.class);

        // when
        scheduler.sendMenuFavoritePushes();

        // then
        verify(service).sendTodayPushes();
        assertThat(scheduled.cron()).isEqualTo("0 0 11 * * *");
        assertThat(scheduled.zone()).isEqualTo("Asia/Seoul");
    }
}
