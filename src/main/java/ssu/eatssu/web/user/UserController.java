package ssu.eatssu.web.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.UserService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.user.dto.*;

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
     * 회원가입
     * <p>자체 회원가입</p>
     *
     * @deprecated 현재는 카카오, 애플 로그인만 사용 중입니다.
     */
    @Operation(summary = "회원가입", description = "회원가입.")
    @PostMapping("/join")
    public BaseResponse<Tokens> join(@Valid @RequestBody Join join) throws JsonProcessingException {
        Tokens tokens = userService.join(join.getEmail(), join.getPwd());
        return BaseResponse.success(tokens);
    }

    /**
     * 이메일 중복 검사
     * <p>중복되지 않은 이메일인 경우 true를 반환합니다.</p>
     */
    @Operation(summary = "이메일 중복 체크", description = "통과하면 true, 존재하는 이메일이면 errorCode 2011")
    @PostMapping("/user-emails/{email}/exist")
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
     * 공통 로그인
     * <p><s>자체, </s>카카오, 애플 로그인 공용으로 사용합니다.</p>
     */
    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/login")
    public BaseResponse<Tokens> login(@Valid @RequestBody Login login) throws JsonProcessingException { // todo:
        // exceiption service단에서 처리
        Tokens tokens = userService.login(login.getEmail(), login.getPwd());
        return BaseResponse.success(tokens);
    }

    /**
     * 닉네임 수정
     * <p>닉네임을 수정합니다.</p>
     */
    @Operation(summary = "닉네임 수정", description = "닉네임 수정")
    @PatchMapping("/nickname")
    public BaseResponse updateNickname(@Valid @RequestBody NicknameEdit nicknameEdit) {
        Long userId = getLoginUserId();
        userService.updateNickname(userId, nicknameEdit.getNickname());
        return BaseResponse.success();
    }

    /**
     * 닉네임 중복 검사
     * <p>중복되지 않은 닉네임인 경우 true를 반환합니다.</p>
     */
    @Operation(summary = "닉네임 중복 체크", description = "통과하면 true, 존재하는 닉네임이면 errorCode 2012")
    @GetMapping("/check-nickname")
    public BaseResponse<Boolean> checkNicknameDuplicate(@Parameter(description = "닉네임")
                                                        @RequestParam(value = "nickname") String nickname) {
        boolean duplicated = userRepository.existsByNickname(nickname);
        if (!duplicated) {
            return BaseResponse.success(true);
        } else {
            //throw new BaseException(NICKNAME_DUPLICATE);
            return BaseResponse.success(false);
        }
    }

    /**
     * 비밀번호 변경
     * <p>비밀번호를 변경합니다.</p>
     */
    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PatchMapping("/password")
    public BaseResponse updatePassword(@Valid @RequestBody PasswordChange passwordChange) {
        Long userId = getLoginUserId();
        userService.updatePassword(userId, passwordChange.getPwd());
        return BaseResponse.success();
    }

    /**
     * AccessToken, RefreshToken 재발급
     * <p>유효한 Token을 받으면 AccessToken, RefreshToken을 새로 발급합니다.</p>
     */
    @Operation(summary = "accessToken, refreshToken 재발급", description = "accessToken, refreshToken 재발급")
    @PostMapping("/token/reissue")
    public BaseResponse<Tokens> refreshToken() throws JsonProcessingException {
        Tokens tokens = userService.refreshTokens(getLoginUser());
        return BaseResponse.success(tokens);
    }

    /**
     * 유저 탈퇴
     * <p>유저를 탈퇴처리합니다.</p>
     */
    @Operation(summary = "유저 탈퇴", description = "탈퇴 성공하면 true 반환")
    @DeleteMapping("/signout")
    public BaseResponse<Boolean> signout() {
        userService.signout(getLoginUserId());
        return BaseResponse.success(true);
    }

}
