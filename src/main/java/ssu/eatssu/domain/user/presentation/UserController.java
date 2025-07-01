package ssu.eatssu.domain.user.presentation;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.PartnershipResponse;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.slice.service.SliceService;
import ssu.eatssu.domain.user.dto.DepartmentResponse;
import ssu.eatssu.domain.user.dto.MyPageResponse;
import ssu.eatssu.domain.user.dto.MyReviewDetail;
import ssu.eatssu.domain.user.dto.NicknameUpdateRequest;
import ssu.eatssu.domain.user.dto.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "User", description = "유저 API")
public class UserController {

    private final UserService userService;
    private final SliceService sliceService;
    private final PartnershipService partnershipService;

    @Operation(summary = "이메일 중복 체크", description = """
            이메일 중복 체크 API 입니다.<br><br>
            중복되지 않은 이메일이면 true 를 반환합니다
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복되지 않은 이메일")
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
            @ApiResponse(responseCode = "200", description = "중복되지 않은 닉네임")
    })
    @GetMapping("/validate/nickname")
    public BaseResponse<Boolean> validateDuplicatedNickname(@Parameter(description = "닉네임")
                                                            @RequestParam(value = "nickname") String nickname) {
        return BaseResponse.success(userService.validateDuplicatedNickname(nickname));
    }

    @Operation(summary = "닉네임 수정", description = "닉네임 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @PatchMapping("/nickname")
    public BaseResponse<?> updateNickname(
            @Valid @RequestBody NicknameUpdateRequest updateNicknameRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateNickname(userDetails, updateNicknameRequest);
        return BaseResponse.success();
    }

    @Operation(summary = "유저 탈퇴", description = "유저 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @DeleteMapping("")
    public BaseResponse<Boolean> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.withdraw(userDetails));
    }

    @Operation(summary = "내가 쓴 리뷰 리스트 조회", description = "내가 쓴 리뷰 리스트를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 쓴 리뷰 리스트 조회 성공"),
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
            @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    @GetMapping("/mypage")
    public BaseResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return BaseResponse.success(userService.findMyPage(customUserDetails));
    }

    @Operation(summary = "유저가 찜한 제휴 조회", description = "유저가 찜한 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 찜한 제휴 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/partnerships")
    public BaseResponse<List<PartnershipResponse>> getUserLikedPartnerships(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getUserLikedPartnerships(userDetails));
    }

    @Operation(summary = "유저의 학과 등록", description = "유저의 학과를 등록하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저의 학과 등록 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 학과", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    @PostMapping("/department")
    public BaseResponse<?> registerDepartment(@RequestBody UpdateDepartmentRequest request,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.registerDepartment(request, userDetails);
        return BaseResponse.success();
    }

    @Operation(summary = "유저의 단과대/학과 제휴 조회", description = "유저의 단과대/학과 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저의 단과대/학과 제휴 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "유저의 학과 정보가 등록되지 않음", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/department/partnerships")
    public BaseResponse<List<PartnershipResponse>> getUserDepartmentPartnerships(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getUserDepartmentPartnerships(userDetails));
    }

    @Operation(summary = "학과 기입 여부 체크", description = """
            학과 기입 여부 체크 API 입니다.<br><br>
            학과를 기입했으면 true, 아니면 false 를 반환합니다
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "학과 기입함")
    })
    @GetMapping("/validate/department")
    public BaseResponse<Boolean> validateDepartmentExists(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.validateDepartmentExists(userDetails));
    }

    @Operation(summary = "유저의 학과 조회", description = "유저의 학과를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저의 학과 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "유저의 학과 정보가 등록되지 않음", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    @GetMapping("/department")
    public BaseResponse<DepartmentResponse> getDepartment(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.getDepartment(userDetails));
    }
}
