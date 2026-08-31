package ssu.eatssu.domain.auth.dto.request;

import org.junit.jupiter.api.Test;
import ssu.eatssu.domain.user.entity.DeviceType;

import static org.assertj.core.api.Assertions.assertThat;

class AuthV2RequestTest {

    @Test
    void appleRequestStoresIdentityTokenAndDeviceType() {
        AppleLoginRequestV2 request = new AppleLoginRequestV2("identity-token", DeviceType.IOS);

        assertThat(request.identityToken()).isEqualTo("identity-token");
        assertThat(request.deviceType()).isEqualTo(DeviceType.IOS);
    }

    @Test
    void kakaoRequestStoresEmailProviderAndDeviceType() {
        KakaoLoginRequestV2 request = new KakaoLoginRequestV2("user@eatssu.com", "provider-id", DeviceType.ANDROID);

        assertThat(request.email()).isEqualTo("user@eatssu.com");
        assertThat(request.providerId()).isEqualTo("provider-id");
        assertThat(request.deviceType()).isEqualTo(DeviceType.ANDROID);
    }
}
