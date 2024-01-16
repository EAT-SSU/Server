package ssu.eatssu.domain.user.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.domain.user.dto.NicknameEdit;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.handler.response.BaseException;
import ssu.eatssu.handler.response.BaseResponse;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.utils.SecurityUtil;

import static ssu.eatssu.utils.SecurityUtil.getLoginUser;
import static ssu.eatssu.utils.SecurityUtil.getLoginUserId;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    /**
     * 이메일 중복 검사
     * <p>중복되지 않은 이메일인 경우 true 를 반환합니다.</p>
     */
    @Operation(summary = "이메일 중복 체크", description = """
        이메일 중복 체크 API 입니다.<br><br>
        중복되지 않은 이메일이면 true 를 반환합니다
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복되지 않은 이메일", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/user-emails/{email}/exist") //todo: 중복인 경우 error throw, 중복 아니면 ApiReposne return
    public BaseResponse<Boolean> checkEmailDuplicate(@Parameter(description = "이메일") @PathVariable String email) {
        boolean duplicated = userRepository.existsByEmail(email);
        if (!duplicated) {
            return BaseResponse.success(true);
        } else {
            //throw new BaseException(EMAIL_DUPLICATE);
            return BaseResponse.success(false);
        }
    }

    /**
     * 닉네임 수정
     * <p>닉네임을 수정합니다.</p>
     */
    @Operation(summary = "닉네임 수정", description = "닉네임 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 수정 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/nickname")
    public BaseResponse<?> updateNickname(@Valid @RequestBody NicknameEdit nicknameEdit) {
        userService.updateNickname(getLoginUserId(), nicknameEdit.getNickname());
        return BaseResponse.success();
    }

    /**
     * 닉네임 중복 검사
     * <p>중복되지 않은 닉네임인 경우 true 를 반환합니다.</p>
     */
    @Operation(summary = "닉네임 중복 체크", description = """
        닉네임 중복 체크 API 입니다.<br><br>
        중복되지 않은 닉네임이면 true 를 반환합니다
        """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복되지 않은 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/check-nickname")
    public BaseResponse<Boolean> checkNicknameDuplicate(@Parameter(description = "닉네임")
                                                        @RequestParam(value = "nickname") String nickname) {
        boolean duplicated = userRepository.existsByNickname(nickname);
        if (duplicated) {
            //throw new BaseException(NICKNAME_DUPLICATE);
            return BaseResponse.success(false);
        } else {
            return BaseResponse.success(true);
        }
    }

    /**
     * AccessToken, RefreshToken 재발급
     * <p>유효한 Token을 받으면 AccessToken, RefreshToken을 새로 발급합니다.</p>
     */
    @Operation(summary = "토큰 재발급", description = "accessToken, refreshToken 재발급 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/token/reissue")
    public BaseResponse<Tokens> refreshToken() {
        Tokens tokens = userService.refreshTokens(getLoginUser());
        return BaseResponse.success(tokens);
    }

    /**
     * 유저 탈퇴
     * <p>유저를 탈퇴처리합니다.</p>
     */
    @Operation(summary = "유저 탈퇴", description = "유저 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 탈퇴 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("/signout")
    public BaseResponse<Boolean> signout() {
        userService.signout(getLoginUserId());
        return BaseResponse.success(true);
    }

}
