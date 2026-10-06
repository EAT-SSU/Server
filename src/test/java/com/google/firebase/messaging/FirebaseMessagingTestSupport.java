package com.google.firebase.messaging;

import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseException;
import org.springframework.test.util.ReflectionTestUtils;

public final class FirebaseMessagingTestSupport {

    private FirebaseMessagingTestSupport() {
    }

    public static SendResponse success() {
        return SendResponse.fromMessageId("projects/test/messages/1");
    }

    public static SendResponse failure(MessagingErrorCode errorCode) {
        return SendResponse.fromException(exception(errorCode));
    }

    public static FirebaseMessagingException exception(MessagingErrorCode errorCode) {
        FirebaseException base = new FirebaseException(ErrorCode.UNKNOWN, "error", null);
        return FirebaseMessagingException.withMessagingErrorCode(base, errorCode);
    }

    public static String tokenOf(Message message) {
        return message.getToken();
    }

    public static String titleOf(Message message) {
        return (String) ReflectionTestUtils.getField(message.getNotification(), "title");
    }

    public static String bodyOf(Message message) {
        return (String) ReflectionTestUtils.getField(message.getNotification(), "body");
    }

    public static String dataOf(Message message, String key) {
        return message.getData().get(key);
    }
}
