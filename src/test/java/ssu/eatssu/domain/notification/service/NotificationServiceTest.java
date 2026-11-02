package ssu.eatssu.domain.notification.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.dto.response.NotificationResponse;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.entity.NotificationType;
import ssu.eatssu.domain.notification.persistence.NotificationRepository;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationServiceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final NotificationService service = new NotificationService(notificationRepository, userRepository,
                                                                        new MenuFavoritePushMessageFactory());

    @Test
    void getNotificationsRendersInCurrentLanguageWithinSevenDays() {
        // given
        User user = user(Language.EN);
        Notification today = notification(user, 10L, TODAY, TimePart.DINNER);
        Notification past = notification(user, 9L, TODAY.minusDays(6), TimePart.LUNCH);
        past.read();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findRecentWithMenus(1L, TODAY.minusDays(6))).thenReturn(List.of(today, past));

        // when
        List<NotificationResponse> responses = service.getNotifications(userDetails(), TODAY);

        // then
        assertThat(responses).extracting(NotificationResponse::notificationId).containsExactly(10L, 9L);
        assertThat(responses.get(0).type()).isEqualTo(NotificationType.MENU_FAVORITE);
        assertThat(responses.get(0).body()).isEqualTo("Today’s Dinner: Charcoal Bulgogi at Student cafeteria 👀");
        assertThat(responses.get(0).date()).isEqualTo("20261102");
        assertThat(responses.get(0).timePart()).isEqualTo(TimePart.DINNER);
        assertThat(responses.get(0).isRead()).isFalse();
        assertThat(responses.get(1).body()).isEqualTo("Lunch on Oct 27: Charcoal Bulgogi at Student cafeteria 👀");
        assertThat(responses.get(1).isRead()).isTrue();
    }

    @Test
    void getNotificationsFollowsLanguageChange() {
        // given
        User user = user(Language.KO);
        Notification notification = notification(user, 1L, TODAY, TimePart.LUNCH);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(notificationRepository.findRecentWithMenus(1L, TODAY.minusDays(6))).thenReturn(List.of(notification));
        String korean = service.getNotifications(userDetails(), TODAY).get(0).body();

        // when
        user.updateLanguage(Language.JA);
        String japanese = service.getNotifications(userDetails(), TODAY).get(0).body();

        // then
        assertThat(korean).isEqualTo("오늘 점심, 학생식당에 연탄불고기 등장 👀");
        assertThat(japanese).isEqualTo("今日の昼食、学生食堂にCharcoal Bulgogiが登場 👀");
    }

    @Test
    void hasUnreadChecksOnlyRecentSevenDays() {
        // given
        when(notificationRepository.existsUnread(1L, TODAY.minusDays(6))).thenReturn(true);

        // when & then
        assertThat(service.hasUnread(userDetails(), TODAY).hasUnread()).isTrue();
    }

    @Test
    void readMarksOwnNotificationAsRead() {
        // given
        Notification notification = notification(user(Language.KO), 5L, TODAY, TimePart.LUNCH);
        when(notificationRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.of(notification));

        // when
        service.read(userDetails(), 5L);

        // then
        assertThat(notification.isRead()).isTrue();
    }

    @Test
    void readFailsForMissingOrOthersNotification() {
        // given
        when(notificationRepository.findByIdAndUserId(5L, 1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.read(userDetails(), 5L))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.NOT_FOUND_NOTIFICATION);
    }

    @Test
    void readAllMarksEveryNotificationOfUser() {
        // when
        service.readAll(userDetails());

        // then
        verify(notificationRepository).markAllRead(1L);
    }

    @Test
    void deleteExpiredNotificationsRemovesOlderThanSevenDays() {
        // given
        when(notificationRepository.deleteAllByTargetDateBefore(TODAY.minusDays(6))).thenReturn(3);

        // when
        int deleted = service.deleteExpiredNotifications(TODAY);

        // then
        assertThat(deleted).isEqualTo(3);
    }

    private Notification notification(User user, Long id, LocalDate date, TimePart timePart) {
        Notification notification = Notification.menuFavorite(user, date, List.of(
                new MenuFavoriteItem("연탄불고기", "Charcoal Bulgogi", Restaurant.HAKSIK, timePart)));
        ReflectionTestUtils.setField(notification, "id", id);
        return notification;
    }

    private User user(Language language) {
        User user = User.create("user@eatssu.com", "tester", OAuthProvider.KAKAO, "provider", "credentials");
        ReflectionTestUtils.setField(user, "id", 1L);
        user.updateLanguage(language);
        return user;
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
