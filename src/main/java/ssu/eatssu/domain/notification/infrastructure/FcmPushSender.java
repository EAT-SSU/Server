package ssu.eatssu.domain.notification.infrastructure;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FcmPushSender {

    private static final int MAX_BATCH_SIZE = 500;
    private static final Set<MessagingErrorCode> INVALID_TOKEN_ERRORS =
            Set.of(MessagingErrorCode.UNREGISTERED, MessagingErrorCode.SENDER_ID_MISMATCH);

    private final ObjectProvider<FirebaseMessaging> firebaseMessagingProvider;

    public List<String> send(List<PushDelivery> deliveries) {
        FirebaseMessaging firebaseMessaging = firebaseMessagingProvider.getIfAvailable();
        if (firebaseMessaging == null) {
            log.warn("Firebase 자격 증명이 없어 푸시 발송을 건너뜁니다. count={}", deliveries.size());
            return List.of();
        }

        List<String> invalidTokens = new ArrayList<>();
        for (int from = 0; from < deliveries.size(); from += MAX_BATCH_SIZE) {
            List<PushDelivery> chunk = deliveries.subList(from, Math.min(from + MAX_BATCH_SIZE, deliveries.size()));
            invalidTokens.addAll(sendChunk(firebaseMessaging, chunk));
        }
        return invalidTokens;
    }

    private List<String> sendChunk(FirebaseMessaging firebaseMessaging, List<PushDelivery> chunk) {
        try {
            BatchResponse response = firebaseMessaging.sendEach(chunk.stream().map(this::toMessage).toList());
            log.info("푸시 발송 결과. success={}, failure={}", response.getSuccessCount(), response.getFailureCount());
            return collectInvalidTokens(chunk, response.getResponses());
        } catch (FirebaseMessagingException e) {
            log.error("푸시 발송 요청 실패. count={}", chunk.size(), e);
            return List.of();
        }
    }

    private List<String> collectInvalidTokens(List<PushDelivery> chunk, List<SendResponse> responses) {
        List<String> invalidTokens = new ArrayList<>();
        for (int i = 0; i < responses.size(); i++) {
            SendResponse response = responses.get(i);
            if (!response.isSuccessful() && isInvalidToken(response.getException())) {
                invalidTokens.add(chunk.get(i).token());
            }
        }
        return invalidTokens;
    }

    private boolean isInvalidToken(FirebaseMessagingException exception) {
        return exception != null && INVALID_TOKEN_ERRORS.contains(exception.getMessagingErrorCode());
    }

    private Message toMessage(PushDelivery delivery) {
        PushMessage message = delivery.message();
        return Message.builder()
                      .setToken(delivery.token())
                      .setNotification(Notification.builder()
                                                   .setTitle(message.title())
                                                   .setBody(message.body())
                                                   .build())
                      .putAllData(message.data())
                      .setAndroidConfig(AndroidConfig.builder().setPriority(AndroidConfig.Priority.HIGH).build())
                      .setApnsConfig(ApnsConfig.builder().setAps(Aps.builder().setSound("default").build()).build())
                      .build();
    }
}
