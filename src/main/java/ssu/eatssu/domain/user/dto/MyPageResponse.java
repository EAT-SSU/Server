package ssu.eatssu.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ssu.eatssu.domain.auth.entity.OAuthProvider;

@AllArgsConstructor
@Builder
@Schema(title = "마이페이지 정보")
@Getter
public class MyPageResponse {
    @Schema(description = "닉네임", example = "피치푸치")
    private String nickname;

    @Schema(description = "연결 계정 정보", example = "피치푸치")
    private OAuthProvider provider;
}
