package ssu.eatssu.domain.notification.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import ssu.eatssu.domain.auth.entity.OAuthProvider;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.infrastructure.FcmPushSender;
import ssu.eatssu.domain.notification.infrastructure.PushDelivery;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatch;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatchQueryRepository;
import ssu.eatssu.domain.notification.persistence.MenuFavoritePushHistoryRepository;
import ssu.eatssu.domain.notification.persistence.PushTokenRepository;
import ssu.eatssu.domain.notification.persistence.UserPushToken;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MenuFavoritePushServiceTest {

    private static final LocalDate DATE = LocalDate.of(2026, 9, 28);

    private final MenuFavoriteMatchQueryRepository matchQueryRepository = mock(MenuFavoriteMatchQueryRepository.class);
    private final MenuFavoritePushHistoryRepository historyRepository = mock(MenuFavoritePushHistoryRepository.class);
    private final PushTokenRepository pushTokenRepository = mock(PushTokenRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final MenuFavoritePushRecorder recorder = mock(MenuFavoritePushRecorder.class);
    private final FcmPushSender fcmPushSender = mock(FcmPushSender.class);
    private final MenuFavoritePushService service = new MenuFavoritePushService(
            matchQueryRepository, historyRepository, pushTokenRepository, userRepository,
            new MenuFavoritePushMessageFactory(), recorder, fcmPushSender);

    @Test
    void sendsLocalizedPushPerTokenAndRecordsHistoryWithNotification() {
        // given
        User korean = user(1L, Language.KO);
        User english = user(2L, Language.EN);
        when(historyRepository.findUserIdsByPushDate(DATE)).thenReturn(Set.of(3L));
        when(matchQueryRepository.findMatches(DATE, List.of(TimePart.LUNCH, TimePart.DINNER))).thenReturn(List.of(
                new MenuFavoriteMatch(1L, "파스타", "Pasta", Restaurant.DODAM, TimePart.LUNCH),
                new MenuFavoriteMatch(1L, "돈가스", null, Restaurant.HAKSIK, TimePart.DINNER),
                new MenuFavoriteMatch(2L, "라면", "Ramen", Restaurant.DORMITORY, TimePart.LUNCH),
                new MenuFavoriteMatch(3L, "김밥", null, Restaurant.HAKSIK, TimePart.LUNCH)));
        when(pushTokenRepository.findAllByUserIds(anyCollection())).thenReturn(List.of(
                new UserPushToken(1L, "phone"),
                new UserPushToken(1L, "tablet"),
                new UserPushToken(2L, "english-phone")));
        when(userRepository.findAllById(anyCollection())).thenReturn(List.of(korean, english));
        assignNotificationIds();
        when(fcmPushSender.send(anyList())).thenReturn(List.of("tablet"));

        // when
        service.sendPushes(DATE);

        // then
        ArgumentCaptor<List<PushDelivery>> deliveries = ArgumentCaptor.forClass(List.class);
        verify(fcmPushSender).send(deliveries.capture());
        assertThat(deliveries.getValue()).extracting(PushDelivery::token)
                                         .containsExactly("phone", "tablet", "english-phone");
        assertThat(deliveries.getValue().get(0).message().body())
                .isEqualTo("오늘 점심·저녁, 도담식당 파스타 · 학생식당 돈가스 등장 👀");
        assertThat(deliveries.getValue().get(0).message().data()).containsEntry("notificationId", "1");
        assertThat(deliveries.getValue().get(2).message().body())
                .isEqualTo("Today’s Lunch: Ramen at Dormitory cafeteria 👀");
        assertThat(deliveries.getValue().get(2).message().data()).containsEntry("notificationId", "2");

        ArgumentCaptor<List<MenuFavoritePushHistory>> histories = ArgumentCaptor.forClass(List.class);
        ArgumentCaptor<List<Notification>> notifications = ArgumentCaptor.forClass(List.class);
        verify(recorder).record(histories.capture(), notifications.capture());
        assertThat(histories.getValue()).extracting(MenuFavoritePushHistory::getPushDate).containsOnly(DATE);
        assertThat(notifications.getValue()).hasSize(2);
        assertThat(notifications.getValue().get(0).toItems()).hasSize(2);
        assertThat(notifications.getValue().get(0).getTargetDate()).isEqualTo(DATE);

        verify(pushTokenRepository).deleteAllByTokens(List.of("tablet"));
    }

    @Test
    void skipsUsersWithoutTokens() {
        // given
        when(historyRepository.findUserIdsByPushDate(DATE)).thenReturn(Set.of());
        when(matchQueryRepository.findMatches(any(), anyCollection()))
                .thenReturn(List.of(new MenuFavoriteMatch(1L, "파스타", null, Restaurant.DODAM, TimePart.LUNCH)));
        when(pushTokenRepository.findAllByUserIds(anyCollection())).thenReturn(List.of());
        when(userRepository.findAllById(anyCollection())).thenReturn(List.of());

        // when
        service.sendPushes(DATE);

        // then
        verify(recorder, never()).record(anyList(), anyList());
        verify(fcmPushSender, never()).send(anyList());
    }

    @Test
    void doesNothingWhenNoFavoriteMenuIsServed() {
        // given
        when(historyRepository.findUserIdsByPushDate(DATE)).thenReturn(Set.of());
        when(matchQueryRepository.findMatches(any(), anyCollection())).thenReturn(List.of());

        // when
        service.sendPushes(DATE);

        // then
        verify(pushTokenRepository, never()).findAllByUserIds(anyCollection());
        verify(fcmPushSender, never()).send(anyList());
        verify(recorder, never()).record(anyList(), anyList());
    }

    @Test
    void keepsTokensWhenEveryDeliverySucceeds() {
        // given
        when(historyRepository.findUserIdsByPushDate(DATE)).thenReturn(Set.of());
        when(matchQueryRepository.findMatches(any(), anyCollection()))
                .thenReturn(List.of(new MenuFavoriteMatch(1L, "파스타", null, Restaurant.DODAM, TimePart.LUNCH)));
        when(pushTokenRepository.findAllByUserIds(anyCollection())).thenReturn(List.of(new UserPushToken(1L, "phone")));
        when(userRepository.findAllById(anyCollection())).thenReturn(List.of(user(1L, Language.KO)));
        assignNotificationIds();
        when(fcmPushSender.send(anyList())).thenReturn(List.of());

        // when
        service.sendPushes(DATE);

        // then
        verify(pushTokenRepository, never()).deleteAllByTokens(anyCollection());
    }

    @Test
    void sendTodayPushesUsesTodayInSeoul() {
        // given
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        when(historyRepository.findUserIdsByPushDate(any())).thenReturn(Set.of());
        when(matchQueryRepository.findMatches(any(), anyCollection())).thenReturn(List.of());

        // when
        service.sendTodayPushes();

        // then
        verify(historyRepository).findUserIdsByPushDate(today);
    }

    private void assignNotificationIds() {
        AtomicLong sequence = new AtomicLong();
        doAnswer(invocation -> {
            List<Notification> notifications = invocation.getArgument(1);
            notifications.forEach(notification -> ReflectionTestUtils.setField(notification, "id",
                                                                               sequence.incrementAndGet()));
            return null;
        }).when(recorder).record(anyList(), anyList());
    }

    private User user(Long id, Language language) {
        User user = User.create("user" + id + "@eatssu.com", "tester", OAuthProvider.KAKAO, "provider" + id,
                                "credentials");
        ReflectionTestUtils.setField(user, "id", id);
        user.updateLanguage(language);
        return user;
    }
}
