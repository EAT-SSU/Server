package ssu.eatssu.domain.notification.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ssu.eatssu.domain.notification.service.MenuFavoritePushService;

@Component
@RequiredArgsConstructor
public class MenuFavoritePushScheduler {

    private final MenuFavoritePushService menuFavoritePushService;

    @Scheduled(cron = "0 0 11 * * *", zone = "Asia/Seoul")
    public void sendMenuFavoritePushes() {
        menuFavoritePushService.sendTodayPushes();
    }
}
