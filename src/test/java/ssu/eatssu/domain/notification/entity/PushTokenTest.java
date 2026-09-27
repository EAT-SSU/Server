package ssu.eatssu.domain.notification.entity;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.User;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PushTokenTest {

    @Test
    void ofCreatesTokenForUserAndDevice() {
        // given
        User user = mock(User.class);

        // when
        PushToken pushToken = PushToken.of(user, "token", DeviceType.IOS);

        // then
        assertThat(pushToken.getId()).isNull();
        assertThat(pushToken.getUser()).isSameAs(user);
        assertThat(pushToken.getToken()).isEqualTo("token");
        assertThat(pushToken.getDeviceType()).isEqualTo(DeviceType.IOS);
    }

    @Test
    void reassignMovesTokenToAnotherUserAndDevice() {
        // given
        PushToken pushToken = PushToken.of(mock(User.class), "token", DeviceType.IOS);
        User newUser = mock(User.class);

        // when
        pushToken.reassign(newUser, DeviceType.ANDROID);

        // then
        assertThat(pushToken.getUser()).isSameAs(newUser);
        assertThat(pushToken.getDeviceType()).isEqualTo(DeviceType.ANDROID);
        assertThat(pushToken.getToken()).isEqualTo("token");
    }

    @Test
    void pushHistoryOfRecordsUserAndDate() {
        // given
        User user = mock(User.class);
        LocalDate date = LocalDate.of(2026, 9, 28);

        // when
        MenuFavoritePushHistory history = MenuFavoritePushHistory.of(user, date);

        // then
        assertThat(history.getId()).isNull();
        assertThat(history.getUser()).isSameAs(user);
        assertThat(history.getPushDate()).isEqualTo(date);
    }
}
