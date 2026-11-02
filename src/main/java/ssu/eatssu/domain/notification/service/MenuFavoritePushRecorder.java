package ssu.eatssu.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.persistence.MenuFavoritePushHistoryRepository;
import ssu.eatssu.domain.notification.persistence.NotificationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MenuFavoritePushRecorder {

    private final MenuFavoritePushHistoryRepository menuFavoritePushHistoryRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void record(List<MenuFavoritePushHistory> histories, List<Notification> notifications) {
        menuFavoritePushHistoryRepository.saveAll(histories);
        notificationRepository.saveAll(notifications);
    }
}
