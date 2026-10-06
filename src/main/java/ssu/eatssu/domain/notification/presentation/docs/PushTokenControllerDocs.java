package ssu.eatssu.domain.notification.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.notification.dto.request.DeletePushTokenRequest;
import ssu.eatssu.domain.notification.dto.request.PushTokenRequest;
import ssu.eatssu.global.handler.response.BaseResponse;

@Tag(name = "PushToken", description = "푸시 토큰 API")
public interface PushTokenControllerDocs {

    @Operation(summary = "푸시 토큰 등록", description = """
            FCM 디바이스 토큰을 등록하거나 갱신하는 API 입니다.<br><br>
            앱 실행/로그인 시, 그리고 FCM 토큰이 갱신될 때 호출합니다.<br>
            한 사용자가 여러 기기를 쓰면 기기마다 등록합니다. 다른 계정에 등록된 토큰이면 현재 계정으로 옮겨집니다.
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "푸시 토큰 등록 성공"),
            @ApiResponse(responseCode = "400", description = "토큰 또는 기기 종류 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> registerToken(PushTokenRequest request, CustomUserDetails userDetails);

    @Operation(summary = "푸시 토큰 삭제", description = "로그아웃 시 해당 기기의 FCM 토큰을 삭제하는 API 입니다. 등록되지 않은 토큰이어도 성공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "푸시 토큰 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "토큰 누락", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> deleteToken(DeletePushTokenRequest request, CustomUserDetails userDetails);
}
