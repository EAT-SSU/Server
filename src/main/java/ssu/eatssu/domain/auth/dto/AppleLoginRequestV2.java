package ssu.eatssu.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.global.log.annotation.LogMask;

// 애플 계정을 통해서 갤럭시 기기에도 접속을 할 수도 있다고 생각해서, DeviceType을 받도록 설계
@Schema(title = "애플 로그인 및 회원가입 V2")
public record AppleLoginRequestV2(
        @LogMask
        @Schema(description = "identityToken", example = "eyJraWQiOiJXNldjT0tCIiwiYWxnIjoi...")
        String identityToken,
        @Schema(description = "deviceType", example = "IOS")
        DeviceType deviceType
) {}
