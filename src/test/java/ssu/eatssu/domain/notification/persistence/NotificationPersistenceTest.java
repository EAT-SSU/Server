package ssu.eatssu.domain.notification.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.support.TransactionTemplate;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.service.MenuFavoriteItem;
import ssu.eatssu.domain.notification.service.MenuFavoritePushRecorder;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class NotificationPersistenceTest {

    private static final LocalDate TODAY = LocalDate.of(2026, 11, 2);

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private MenuFavoritePushHistoryRepository menuFavoritePushHistoryRepository;

    @Autowired
    private MenuFavoritePushRecorder menuFavoritePushRecorder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @BeforeEach
    void setUp() {
        cleanUp();
    }

    @AfterEach
    void tearDown() {
        cleanUp();
    }

    private void cleanUp() {
        notificationRepository.deleteAll();
        menuFavoritePushHistoryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void recorderSavesHistoryAndNotificationWithMenus() {
        // given
        User user = userRepository.save(user("record@eatssu.com"));
        Notification notification = Notification.menuFavorite(user, TODAY, List.of(
                item("연탄불고기", "Charcoal Bulgogi", Restaurant.HAKSIK, TimePart.LUNCH),
                item("배추김치", null, Restaurant.DODAM, TimePart.DINNER)));

        // when
        menuFavoritePushRecorder.record(List.of(MenuFavoritePushHistory.of(user, TODAY)), List.of(notification));

        // then
        assertThat(notification.getId()).isNotNull();
        assertThat(menuFavoritePushHistoryRepository.findUserIdsByPushDate(TODAY)).containsExactly(user.getId());
        List<Notification> found = notificationRepository.findRecentWithMenus(user.getId(), TODAY.minusDays(6));
        assertThat(found).hasSize(1);
        assertThat(found.get(0).toItems()).containsExactly(
                item("연탄불고기", "Charcoal Bulgogi", Restaurant.HAKSIK, TimePart.LUNCH),
                item("배추김치", null, Restaurant.DODAM, TimePart.DINNER));
    }

    @Test
    void findRecentWithMenusReturnsOnlyOwnRecentNotificationsNewestFirst() {
        // given
        User owner = userRepository.save(user("owner@eatssu.com"));
        User stranger = userRepository.save(user("stranger@eatssu.com"));
        Notification today = notificationRepository.save(notification(owner, TODAY));
        Notification oldest = notificationRepository.save(notification(owner, TODAY.minusDays(6)));
        notificationRepository.save(notification(owner, TODAY.minusDays(7)));
        notificationRepository.save(notification(stranger, TODAY));

        // when
        List<Notification> found = notificationRepository.findRecentWithMenus(owner.getId(), TODAY.minusDays(6));

        // then
        assertThat(found).extracting(Notification::getId).containsExactly(today.getId(), oldest.getId());
    }

    @Test
    void readQueriesAreScopedToOwner() {
        // given
        User owner = userRepository.save(user("reader@eatssu.com"));
        User stranger = userRepository.save(user("other@eatssu.com"));
        Notification recent = notificationRepository.save(notification(owner, TODAY));
        notificationRepository.save(notification(owner, TODAY.minusDays(1)));
        notificationRepository.save(notification(stranger, TODAY));

        // when
        boolean unreadBefore = notificationRepository.existsUnread(owner.getId(), TODAY.minusDays(6));
        boolean foundByStranger = notificationRepository.findByIdAndUserId(recent.getId(), stranger.getId())
                                                        .isPresent();
        Integer updated = transactionTemplate.execute(status -> notificationRepository.markAllRead(owner.getId()));
        boolean unreadAfter = notificationRepository.existsUnread(owner.getId(), TODAY.minusDays(6));

        // then
        assertThat(unreadBefore).isTrue();
        assertThat(foundByStranger).isFalse();
        assertThat(updated).isEqualTo(2);
        assertThat(unreadAfter).isFalse();
        assertThat(notificationRepository.existsUnread(stranger.getId(), TODAY.minusDays(6))).isTrue();
    }

    @Test
    void existsUnreadIgnoresNotificationsOlderThanSevenDays() {
        // given
        User user = userRepository.save(user("old@eatssu.com"));
        notificationRepository.save(notification(user, TODAY.minusDays(7)));

        // when & then
        assertThat(notificationRepository.existsUnread(user.getId(), TODAY.minusDays(6))).isFalse();
    }

    @Test
    void deleteAllByTargetDateBeforeRemovesExpiredNotificationsAndMenus() {
        // given
        User user = userRepository.save(user("expire@eatssu.com"));
        notificationRepository.save(notification(user, TODAY.minusDays(7)));
        Notification kept = notificationRepository.save(notification(user, TODAY.minusDays(6)));

        // when
        Integer deleted = transactionTemplate.execute(
                status -> notificationRepository.deleteAllByTargetDateBefore(TODAY.minusDays(6)));

        // then
        assertThat(deleted).isEqualTo(1);
        assertThat(notificationRepository.findAll()).extracting(Notification::getId).containsExactly(kept.getId());
    }

    @Test
    void allowsOneMenuFavoriteNotificationPerUserPerDay() {
        // given
        User user = userRepository.save(user("unique@eatssu.com"));
        notificationRepository.save(notification(user, TODAY));

        // when & then
        assertThatThrownBy(() -> notificationRepository.save(notification(user, TODAY)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void withdrawingUserRemovesNotifications() {
        // given
        User user = userRepository.save(user("withdraw@eatssu.com"));
        notificationRepository.save(notification(user, TODAY));

        // when
        userRepository.deleteById(user.getId());

        // then
        assertThat(notificationRepository.count()).isZero();
    }

    private Notification notification(User user, LocalDate date) {
        return Notification.menuFavorite(user, date, List.of(item("돈까스", null, Restaurant.HAKSIK, TimePart.LUNCH)));
    }

    private MenuFavoriteItem item(String menuName, String menuNameEn, Restaurant restaurant, TimePart timePart) {
        return new MenuFavoriteItem(menuName, menuNameEn, restaurant, timePart);
    }

    private User user(String email) {
        return User.create(email, "tester", OAuthProvider.KAKAO, email, "credentials");
    }
}
