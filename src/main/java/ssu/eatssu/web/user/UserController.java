package ssu.eatssu.web.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.repository.UserRepository;
import ssu.eatssu.response.BaseException;
import ssu.eatssu.response.BaseResponse;
import ssu.eatssu.service.UserService;
import ssu.eatssu.utils.SecurityUtil;
import ssu.eatssu.web.user.dto.*;

import static ssu.eatssu.response.BaseResponseStatus.*;
import static ssu.eatssu.utils.SecurityUtil.getLoginUser;
import static ssu.eatssu.utils.SecurityUtil.getLoginUserId;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name="User",description = "유저 API")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SecurityUtil securityUtil;

    /**
     * 회원가입
     */
    @Operation(summary = "회원가입", description = "회원가입.")
    @PostMapping("/join")
    public ResponseEntity<Tokens> join(@Valid @RequestBody Join join) throws JsonProcessingException {
        Tokens tokens = userService.join(join.getEmail(), join.getPwd());
        return ResponseEntity.ok(tokens);
    }

    /**
     * 이메일 중복체크. 중복이면 true
     */
    @Operation(summary = "이메일 중복 체크", description = "통과하면 true, 존재하는 이메일이면 errorCode 2011")
    @PostMapping("/user-emails/{email}/exist")
    public ResponseEntity checkEmailDuplicate(@Parameter(description = "이메일")@PathVariable String email){
        boolean duplicated = userRepository.existsByEmail(email);
        if(!duplicated){
            return ResponseEntity.ok(true);
        }else{
            throw new BaseException(EMAIL_DUPLICATE);
        }
    }

    /**
     * 로그인
     */
    @Operation(summary = "로그인", description = "로그인")
    @PostMapping("/login")
    public ResponseEntity<Tokens> login(@Valid @RequestBody Login login) throws JsonProcessingException {
        Tokens tokens = userService.login(login.getEmail(), login.getPwd());
        return ResponseEntity.ok(tokens);
    }

    /**
     * 닉네임 수정
     */
    @Operation(summary = "닉네임 수정", description = "닉네임 수정")
    @PatchMapping("/nickname")
    public ResponseEntity nicknameUpdate(@Valid @RequestBody NicknameEdit nicknameEdit){
        Long userId = getLoginUserId();
        userService.updateNickname(userId, nicknameEdit.getNickname());
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * 닉네임 중복 체크
     */
    @Operation(summary = "닉네임 중복 체크", description = "통과하면 true, 존재하는 닉네임이면 errorCode 2012")
    @GetMapping("/check-nickname")
    public ResponseEntity checkNicknameDuplicate(@Parameter(description = "닉네임")@RequestParam(value =
            "nickname") String nickname){
        boolean duplicated = userRepository.existsByNickname(nickname);
        if(!duplicated){
            return ResponseEntity.ok(true);
        }else{
            throw new BaseException(NICKNAME_DUPLICATE);
        }
    }

    /**
     * 비밀번호 변경
     */
    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경")
    @PatchMapping("/password")
    public ResponseEntity passwordChange(@Valid @RequestBody PasswordChange passwordChange){
        Long userId = getLoginUserId();
        userService.changePassword(userId, passwordChange.getPwd());
        return new ResponseEntity(HttpStatus.OK);
    }

    /**
     * accessToken, refreshToken 재발급
     */
    @Operation(summary = "accessToken, refreshToken 재발급", description = "accessToken, refreshToken 재발급")
    @PostMapping("/token/reissue")
    public ResponseEntity<Tokens> refreshAccessToken() throws JsonProcessingException{
        Tokens tokens = userService.refreshAccessToken(getLoginUser());
        return ResponseEntity.ok(tokens);
    }
    /**
     * 유저 탈퇴
     */
    @Operation(summary = "유저 탈퇴", description = "탈퇴 성공하면 true 반환")
    @DeleteMapping("/signout")
    public ResponseEntity signout(){
        userService.signout(getLoginUserId());
        return ResponseEntity.ok(true);
    }

    @ExceptionHandler(BaseException.class)
    public BaseResponse<String> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return new BaseResponse<>(e.getStatus());
    }


}
