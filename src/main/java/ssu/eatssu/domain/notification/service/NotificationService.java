package ssu.eatssu.domain.notification.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.response.NotificationResponse;
import ssu.eatssu.domain.notification.dto.response.UnreadNotificationResponse;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.persistence.NotificationRepository;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_NOTIFICATION;
import static ssu.eatssu.global.handler.response.BaseResponseStatus.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final int RETENTION_DAYS = 7;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final MenuFavoritePushMessageFactory menuFavoritePushMessageFactory;

    public List<NotificationResponse> getNotifications(CustomUserDetails userDetails) {
        return getNotifications(userDetails, today());
    }

    public List<NotificationResponse> getNotifications(CustomUserDetails userDetails, LocalDate today) {
        User user = userRepository.findById(userDetails.getId())
                                  .orElseThrow(() -> new BaseException(NOT_FOUND_USER));
        Language language = user.getLanguage();
        return notificationRepository.findRecentWithMenus(user.getId(), oldestVisibleDate(today)).stream()
                                     .map(notification -> toResponse(notification, today, language))
                                     .toList();
    }

    public UnreadNotificationResponse hasUnread(CustomUserDetails userDetails) {
        return hasUnread(userDetails, today());
    }

    public UnreadNotificationResponse hasUnread(CustomUserDetails userDetails, LocalDate today) {
        return new UnreadNotificationResponse(
                notificationRepository.existsUnread(userDetails.getId(), oldestVisibleDate(today)));
    }

    @Transactional
    public void read(CustomUserDetails userDetails, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUserId(notificationId, userDetails.getId())
                                                          .orElseThrow(
                                                                  () -> new BaseException(NOT_FOUND_NOTIFICATION));
        notification.read();
    }

    @Transactional
    public void readAll(CustomUserDetails userDetails) {
        notificationRepository.markAllRead(userDetails.getId());
    }

    @Transactional
    public int deleteExpiredNotifications() {
        return deleteExpiredNotifications(today());
    }

    @Transactional
    public int deleteExpiredNotifications(LocalDate today) {
        return notificationRepository.deleteAllByTargetDateBefore(oldestVisibleDate(today));
    }

    private NotificationResponse toResponse(Notification notification, LocalDate today, Language language) {
        List<MenuFavoriteItem> items = notification.toItems();
        NotificationText text = menuFavoritePushMessageFactory.createText(notification.getTargetDate(), today, items,
                                                                          language);
        return NotificationResponse.of(notification, text, menuFavoritePushMessageFactory.primaryTimePart(items));
    }

    private LocalDate oldestVisibleDate(LocalDate today) {
        return today.minusDays(RETENTION_DAYS - 1);
    }

    private LocalDate today() {
        return LocalDate.now(SEOUL);
    }
}
