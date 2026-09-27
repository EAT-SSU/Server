package ssu.eatssu.domain.notification.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(title = "푸시 토큰 삭제")
public record DeletePushTokenRequest(
        @Schema(description = "FCM 디바이스 토큰", example = "dXNlci1mY20tdG9rZW4")
        @NotBlank
        String token
) {

}
