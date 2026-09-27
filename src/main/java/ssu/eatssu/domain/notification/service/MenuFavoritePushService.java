package ssu.eatssu.domain.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.entity.MenuFavoritePushHistory;
import ssu.eatssu.domain.notification.infrastructure.FcmPushSender;
import ssu.eatssu.domain.notification.infrastructure.PushDelivery;
import ssu.eatssu.domain.notification.infrastructure.PushMessage;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatch;
import ssu.eatssu.domain.notification.persistence.MenuFavoriteMatchQueryRepository;
import ssu.eatssu.domain.notification.persistence.MenuFavoritePushHistoryRepository;
import ssu.eatssu.domain.notification.persistence.PushTokenRepository;
import ssu.eatssu.domain.notification.persistence.UserPushToken;
import ssu.eatssu.domain.user.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final FcmPushSender fcmPushSender;

    public void sendTodayPushes() {
        sendPushes(LocalDate.now(SEOUL));
    }

    public void sendPushes(LocalDate date) {
        Set<Long> alreadyPushedUserIds = menuFavoritePushHistoryRepository.findUserIdsByPushDate(date);
        Map<Long, List<MenuFavoriteMatch>> matchesByUser =
                menuFavoriteMatchQueryRepository.findMatches(date, TARGET_TIME_PARTS).stream()
                                                .filter(match -> !alreadyPushedUserIds.contains(match.userId()))
                                                .collect(Collectors.groupingBy(MenuFavoriteMatch::userId,
                                                                               LinkedHashMap::new,
                                                                               Collectors.toList()));
        if (matchesByUser.isEmpty()) {
            log.info("찜 메뉴 푸시 대상이 없습니다. date={}", date);
            return;
        }

        Map<Long, List<String>> tokensByUser =
                pushTokenRepository.findAllByUserIds(matchesByUser.keySet()).stream()
                                   .collect(Collectors.groupingBy(UserPushToken::userId,
                                                                  Collectors.mapping(UserPushToken::token,
                                                                                     Collectors.toList())));

        List<PushDelivery> deliveries = new ArrayList<>();
        List<MenuFavoritePushHistory> histories = new ArrayList<>();
        matchesByUser.forEach((userId, matches) -> {
            List<String> tokens = tokensByUser.getOrDefault(userId, List.of());
            if (tokens.isEmpty()) {
                return;
            }
            PushMessage message = menuFavoritePushMessageFactory.create(date, matches);
            tokens.forEach(token -> deliveries.add(new PushDelivery(token, message)));
            histories.add(MenuFavoritePushHistory.of(userRepository.getReferenceById(userId), date));
        });

        menuFavoritePushHistoryRepository.saveAll(histories);
        List<String> invalidTokens = fcmPushSender.send(deliveries);
        if (!invalidTokens.isEmpty()) {
            pushTokenRepository.deleteAllByTokens(invalidTokens);
        }

        log.info("찜 메뉴 푸시 발송 완료. date={}, users={}, deliveries={}, removedTokens={}",
                 date, histories.size(), deliveries.size(), invalidTokens.size());
    }
}
