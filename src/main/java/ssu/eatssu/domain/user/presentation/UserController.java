package ssu.eatssu.domain.user.presentation;

import static ssu.eatssu.domain.auth.infrastructure.SecurityUtil.getLoginUser;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.user.dto.MyReviewDetail;
import ssu.eatssu.domain.user.dto.MyPageResponse;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.slice.service.SliceService;
import ssu.eatssu.domain.user.dto.UpdateNicknameRequest;
import ssu.eatssu.domain.user.dto.Tokens;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.global.handler.response.BaseResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final UserService userService;
    private final SliceService sliceService;

    @Operation(summary = "이메일 중복 체크", description = """
        이메일 중복 체크 API 입니다.<br><br>
        중복되지 않은 이메일이면 true 를 반환합니다
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "중복되지 않은 이메일", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/validate/email/{email}") //todo: 중복인 경우 error throw, 중복 아니면 ApiReposne return
    public BaseResponse<Boolean> validateDuplicatedEmail(
        @Parameter(description = "이메일") @PathVariable String email) {
        return BaseResponse.success(userService.validateDuplicatedEmail(email));
    }

    @Operation(summary = "닉네임 중복 체크", description = """
        닉네임 중복 체크 API 입니다.<br><br>
        중복되지 않은 닉네임이면 true 를 반환합니다
        """)
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "중복되지 않은 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/validate/nickname")
    public BaseResponse<Boolean> validateDuplicatedNickname(@Parameter(description = "닉네임")
    @RequestParam(value = "nickname") String nickname) {
        return BaseResponse.success(userService.validateDuplicatedNickname(nickname));
    }

    @Operation(summary = "닉네임 수정", description = "닉네임 수정 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "닉네임 수정 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/nickname")
    public BaseResponse<?> updateNickname(
        @Valid @RequestBody UpdateNicknameRequest updateNicknameRequest,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateNickname(userDetails, updateNicknameRequest);
        return BaseResponse.success();
    }

    @Operation(summary = "유저 탈퇴", description = "유저 탈퇴 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "유저 탈퇴 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("")
    public BaseResponse<Boolean> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.withdraw(userDetails));
    }

    @Operation(summary = "내가 쓴 리뷰 리스트 조회", description = "내가 쓴 리뷰 리스트를 조회하는 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "내가 쓴 리뷰 리스트 조회 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/reviews")
    public BaseResponse<SliceResponse<MyReviewDetail>> getMyReviewList(
        @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) @RequestParam(required = false) Long lastReviewId,
        @ParameterObject @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<MyReviewDetail> myReviews = sliceService.findMyReviews(customUserDetails,
            pageable,
            lastReviewId);
        return BaseResponse.success(myReviews);
    }

    @Operation(summary = "마이페이지 정보 조회", description = "마이페이지 정보를 조회하는 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/mypage")
    public BaseResponse<MyPageResponse> getMyPage(
        @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return BaseResponse.success(userService.findMyPage(customUserDetails));
    }

    @Operation(summary = "토큰 재발급", description = "accessToken, refreshToken 재발급 API 입니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PostMapping("/token/reissue")
    public BaseResponse<Tokens> refreshToken() {
        Tokens tokens = userService.refreshTokens(getLoginUser());
        return BaseResponse.success(tokens);
    }
}
