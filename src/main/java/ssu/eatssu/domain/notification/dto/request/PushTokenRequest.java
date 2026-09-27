package ssu.eatssu.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import ssu.eatssu.domain.user.entity.DeviceType;

@Schema(title = "푸시 토큰 등록")
public record PushTokenRequest(
        @Schema(description = "FCM 디바이스 토큰", example = "dXNlci1mY20tdG9rZW4")
        @NotBlank
        @Size(max = 512)
        String token,
        @Schema(description = "기기 종류", example = "ANDROID")
        @NotNull
        DeviceType deviceType
) {

}
