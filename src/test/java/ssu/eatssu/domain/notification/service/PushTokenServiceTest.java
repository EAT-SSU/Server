package ssu.eatssu.domain.notification.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.request.DeletePushTokenRequest;
import ssu.eatssu.domain.notification.dto.request.PushTokenRequest;
import ssu.eatssu.domain.notification.entity.PushToken;
import ssu.eatssu.domain.notification.persistence.PushTokenRepository;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;
import ssu.eatssu.domain.user.repository.UserRepository;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PushTokenServiceTest {

    private final PushTokenRepository pushTokenRepository = mock(PushTokenRepository.class);
    private final UserRepository userRepository = mock(UserRepository.class);
    private final PushTokenService service = new PushTokenService(pushTokenRepository, userRepository);
    private final CustomUserDetails userDetails = new CustomUserDetails(1L, "user@eatssu.com", "credentials",
                                                                        Role.USER, null);

    @Test
    void registerTokenSavesNewToken() {
        // given
        User user = mock(User.class);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pushTokenRepository.findByToken("token")).thenReturn(Optional.empty());

        // when
        service.registerToken(userDetails, new PushTokenRequest("token", DeviceType.ANDROID));

        // then
        ArgumentCaptor<PushToken> captor = ArgumentCaptor.forClass(PushToken.class);
        verify(pushTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUser()).isSameAs(user);
        assertThat(captor.getValue().getToken()).isEqualTo("token");
        assertThat(captor.getValue().getDeviceType()).isEqualTo(DeviceType.ANDROID);
    }

    @Test
    void registerTokenReassignsExistingTokenToCurrentUser() {
        // given
        User user = mock(User.class);
        PushToken existing = PushToken.of(mock(User.class), "token", DeviceType.IOS);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(pushTokenRepository.findByToken("token")).thenReturn(Optional.of(existing));

        // when
        service.registerToken(userDetails, new PushTokenRequest("token", DeviceType.ANDROID));

        // then
        assertThat(existing.getUser()).isSameAs(user);
        assertThat(existing.getDeviceType()).isEqualTo(DeviceType.ANDROID);
        verify(pushTokenRepository, never()).save(any());
    }

    @Test
    void registerTokenThrowsWhenUserDoesNotExist() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> service.registerToken(userDetails, new PushTokenRequest("token", DeviceType.IOS)))
                .isInstanceOf(BaseException.class)
                .extracting("status")
                .isEqualTo(BaseResponseStatus.NOT_FOUND_USER);
    }

    @Test
    void deleteTokenRemovesOnlyCurrentUsersToken() {
        // given
        DeletePushTokenRequest request = new DeletePushTokenRequest("token");

        // when
        service.deleteToken(userDetails, request);

        // then
        verify(pushTokenRepository).deleteByUserIdAndToken(1L, "token");
    }
}
