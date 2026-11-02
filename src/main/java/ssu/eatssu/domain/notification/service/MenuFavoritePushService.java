package ssu.eatssu.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;
import ssu.eatssu.domain.notification.entity.Notification;
import ssu.eatssu.domain.notification.infrastructure.FcmPushSender;
import ssu.eatssu.domain.notification.infrastructure.PushDelivery;
import ssu.eatssu.domain.notification.infrastructure.PushMessage;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatch;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatchQueryRepository;
import ssu.eatssu.domain.notification.persistence.MenuFavoritePushHistoryRepository;
import ssu.eatssu.domain.notification.persistence.PushTokenRepository;
import ssu.eatssu.domain.notification.persistence.UserPushToken;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuFavoritePushService {

    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");
    private static final List<TimePart> TARGET_TIME_PARTS = List.of(TimePart.LUNCH, TimePart.DINNER);

    private final MenuFavoriteMatchQueryRepository menuFavoriteMatchQueryRepository;
    private final MenuFavoritePushHistoryRepository menuFavoritePushHistoryRepository;
    private final PushTokenRepository pushTokenRepository;
    private final UserRepository userRepository;
    private final MenuFavoritePushMessageFactory menuFavoritePushMessageFactory;
    private final MenuFavoritePushRecorder menuFavoritePushRecorder;
    private final FcmPushSender fcmPushSender;

    public void sendTodayPushes() {
        sendPushes(LocalDate.now(SEOUL));
    }

    public void sendPushes(LocalDate date) {
        Set<Long> alreadyPushedUserIds = menuFavoritePushHistoryRepository.findUserIdsByPushDate(date);
        Map<Long, List<MenuFavoriteItem>> itemsByUser =
                menuFavoriteMatchQueryRepository.findMatches(date, TARGET_TIME_PARTS).stream()
                                                .filter(match -> !alreadyPushedUserIds.contains(match.userId()))
                                                .collect(Collectors.groupingBy(MenuFavoriteMatch::userId,
                                                                               LinkedHashMap::new,
                                                                               Collectors.mapping(
                                                                                       MenuFavoriteMatch::toItem,
                                                                                       Collectors.toList())));
        if (itemsByUser.isEmpty()) {
            log.info("찜 메뉴 푸시 대상이 없습니다. date={}", date);
            return;
        }

        Map<Long, List<String>> tokensByUser =
                pushTokenRepository.findAllByUserIds(itemsByUser.keySet()).stream()
                                   .collect(Collectors.groupingBy(UserPushToken::userId,
                                                                  Collectors.mapping(UserPushToken::token,
                                                                                     Collectors.toList())));
        Map<Long, User> usersById = userRepository.findAllById(tokensByUser.keySet()).stream()
                                                  .collect(Collectors.toMap(User::getId, Function.identity()));

        List<MenuFavoritePushHistory> histories = new ArrayList<>();
        List<Notification> notifications = new ArrayList<>();
        itemsByUser.forEach((userId, items) -> {
            User user = usersById.get(userId);
            if (user == null) {
                return;
            }
            histories.add(MenuFavoritePushHistory.of(user, date));
            notifications.add(Notification.menuFavorite(user, date, items));
        });
        if (notifications.isEmpty()) {
            log.info("찜 메뉴 푸시를 받을 토큰이 없습니다. date={}", date);
            return;
        }

        menuFavoritePushRecorder.record(histories, notifications);

        List<PushDelivery> deliveries = new ArrayList<>();
        notifications.forEach(notification -> {
            User user = notification.getUser();
            PushMessage message = menuFavoritePushMessageFactory.createPush(date, notification.toItems(),
                                                                            user.getLanguage(),
                                                                            notification.getId());
            tokensByUser.get(user.getId()).forEach(token -> deliveries.add(new PushDelivery(token, message)));
        });

        List<String> invalidTokens = fcmPushSender.send(deliveries);
        if (!invalidTokens.isEmpty()) {
            pushTokenRepository.deleteAllByTokens(invalidTokens);
        }

        log.info("찜 메뉴 푸시 발송 완료. date={}, users={}, deliveries={}, removedTokens={}",
                 date, notifications.size(), deliveries.size(), invalidTokens.size());
    }
}
