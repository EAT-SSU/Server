package ssu.eatssu.domain.notification.infrastructure;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.FirebaseMessagingTestSupport;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.SendResponse;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.ObjectProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FcmPushSenderTest {

    private static final PushMessage MESSAGE = new PushMessage("제목", "본문", Map.of("date", "20260928"));

    private final FirebaseMessaging firebaseMessaging = mock(FirebaseMessaging.class);
    private final ObjectProvider<FirebaseMessaging> provider = mock(ObjectProvider.class);
    private final FcmPushSender sender = new FcmPushSender(provider);

    @Test
    void skipsSendingWhenFirebaseIsNotConfigured() {
        // given
        when(provider.getIfAvailable()).thenReturn(null);

        // when
        List<String> invalidTokens = sender.send(List.of(new PushDelivery("token", MESSAGE)));

        // then
        assertThat(invalidTokens).isEmpty();
    }

    @Test
    void sendsNotificationAndReturnsOnlyUnusableTokens() throws Exception {
        // given
        when(provider.getIfAvailable()).thenReturn(firebaseMessaging);
        BatchResponse response = batch(List.of(
                FirebaseMessagingTestSupport.success(),
                FirebaseMessagingTestSupport.failure(MessagingErrorCode.UNREGISTERED),
                FirebaseMessagingTestSupport.failure(MessagingErrorCode.SENDER_ID_MISMATCH),
                FirebaseMessagingTestSupport.failure(MessagingErrorCode.INTERNAL)));
        when(firebaseMessaging.sendEach(anyList())).thenReturn(response);
        List<PushDelivery> deliveries = List.of(new PushDelivery("ok", MESSAGE), new PushDelivery("expired", MESSAGE),
                                                new PushDelivery("other-project", MESSAGE),
                                                new PushDelivery("retryable", MESSAGE));

        // when
        List<String> invalidTokens = sender.send(deliveries);

        // then
        assertThat(invalidTokens).containsExactly("expired", "other-project");
        ArgumentCaptor<List<Message>> captor = ArgumentCaptor.forClass(List.class);
        verify(firebaseMessaging).sendEach(captor.capture());
        Message first = captor.getValue().get(0);
        assertThat(FirebaseMessagingTestSupport.tokenOf(first)).isEqualTo("ok");
        assertThat(FirebaseMessagingTestSupport.titleOf(first)).isEqualTo("제목");
        assertThat(FirebaseMessagingTestSupport.bodyOf(first)).isEqualTo("본문");
        assertThat(FirebaseMessagingTestSupport.dataOf(first, "date")).isEqualTo("20260928");
    }

    @Test
    void splitsDeliveriesIntoBatchesOfFiveHundred() throws Exception {
        // given
        when(provider.getIfAvailable()).thenReturn(firebaseMessaging);
        List<PushDelivery> deliveries = IntStream.range(0, 501)
                                                 .mapToObj(i -> new PushDelivery("token-" + i, MESSAGE))
                                                 .toList();
        when(firebaseMessaging.sendEach(anyList())).thenAnswer(invocation -> {
            List<Message> messages = invocation.getArgument(0);
            List<SendResponse> responses = new ArrayList<>();
            messages.forEach(message -> responses.add(FirebaseMessagingTestSupport.success()));
            return batch(responses);
        });

        // when
        List<String> invalidTokens = sender.send(deliveries);

        // then
        assertThat(invalidTokens).isEmpty();
        ArgumentCaptor<List<Message>> captor = ArgumentCaptor.forClass(List.class);
        verify(firebaseMessaging, times(2)).sendEach(captor.capture());
        assertThat(captor.getAllValues()).extracting(List::size).containsExactly(500, 1);
    }

    @Test
    void continuesWhenFirebaseRejectsTheWholeBatch() throws Exception {
        // given
        when(provider.getIfAvailable()).thenReturn(firebaseMessaging);
        FirebaseMessagingException exception = FirebaseMessagingTestSupport.exception(MessagingErrorCode.INTERNAL);
        when(firebaseMessaging.sendEach(anyList())).thenThrow(exception);

        // when
        List<String> invalidTokens = sender.send(List.of(new PushDelivery("token", MESSAGE)));

        // then
        assertThat(invalidTokens).isEmpty();
    }

    private BatchResponse batch(List<SendResponse> responses) {
        BatchResponse batchResponse = mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(responses);
        when(batchResponse.getSuccessCount()).thenReturn((int) responses.stream().filter(SendResponse::isSuccessful).count());
        when(batchResponse.getFailureCount()).thenReturn((int) responses.stream().filter(r -> !r.isSuccessful()).count());
        return batchResponse;
    }
}
