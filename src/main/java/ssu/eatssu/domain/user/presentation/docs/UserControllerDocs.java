package ssu.eatssu.domain.user.presentation.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.response.PartnershipResponse;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.user.dto.response.DepartmentResponse;
import ssu.eatssu.domain.user.dto.response.GetCollegeResponse;
import ssu.eatssu.domain.user.dto.response.GetDepartmentResponse;
import ssu.eatssu.domain.user.dto.response.LanguageResponse;
import ssu.eatssu.domain.user.dto.request.LanguageUpdateRequest;
import ssu.eatssu.domain.user.dto.response.MyMealReviewResponse;
import ssu.eatssu.domain.user.dto.response.MyPageResponse;
import ssu.eatssu.domain.user.dto.response.MyReviewDetail;
import ssu.eatssu.domain.user.dto.request.NicknameUpdateRequest;
import ssu.eatssu.domain.user.dto.request.UpdateDepartmentRequest;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@Tag(name = "User", description = "유저 API")
public interface UserControllerDocs {

    @Operation(summary = "이메일 중복 체크", description = """
            이메일 중복 체크 API 입니다.<br><br>
            중복되지 않은 이메일이면 true 를 반환합니다
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복되지 않은 이메일")
    })
    BaseResponse<Boolean> validateDuplicatedEmail(@Parameter(description = "이메일") String email);

    @Operation(summary = "닉네임 중복 및 유효성 체크", description = """
            닉네임 중복 및 유효성 체크 API 입니다.<br><br>
            유효하고 중복되지 않은 닉네임이면 true 를 반환합니다
            """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유효하고 중복되지 않은 닉네임"),
            @ApiResponse(responseCode = "400", description = "숫자로만 이루어진 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "연속된 공백을 포함하는 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "연속된 하이폰을 사용하는 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "첫 글자가 한글,영문,숫자가 아닌 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "마지막 글자가 한글,영문,숫자가 아닌 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "한글,영문,숫자,공백,하이폰(-)이외의 문자를 쓴 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "닉네임이 1자 이상 16이하가 아닌 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "서비스명/브랜드명 단독 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "관리자로 혼동될 수 있는 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "욕설/비속어가 이름에 포함되는 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<Boolean> validateNickname(@Parameter(description = "닉네임") String nickname);

    @Operation(summary = "닉네임 수정", description = "닉네임 수정 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "숫자로만 이루어진 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "연속된 공백을 포함하는 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "연속된 하이폰을 사용하는 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "첫 글자가 한글,영문,숫자가 아닌 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "마지막 글자가 한글,영문,숫자가 아닌 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "한글,영문,숫자,공백,하이폰(-)이외의 문자를 쓴 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "닉네임이 1자 이상 16이하가 아닌 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "서비스명/브랜드명 단독 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "관리자로 혼동될 수 있는 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "욕설/비속어가 이름에 포함되는 경우", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<?> updateNickname(NicknameUpdateRequest updateNicknameRequest, CustomUserDetails userDetails);

    @Operation(summary = "언어 설정 수정", description = "유저의 언어 설정을 수정하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "언어 설정 수정 성공"),
            @ApiResponse(responseCode = "400", description = "지원하지 않는 언어 또는 누락된 언어 설정", content = @Content(schema = @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<?> updateLanguage(LanguageUpdateRequest request, CustomUserDetails userDetails);

    @Operation(summary = "언어 설정 조회", description = "유저의 언어 설정을 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "언어 설정 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<LanguageResponse> getLanguage(CustomUserDetails userDetails);

    @Operation(summary = "유저 탈퇴", description = "유저 탈퇴 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 탈퇴 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<Boolean> withdraw(CustomUserDetails userDetails);

    @Operation(summary = "내가 쓴 리뷰 리스트 조회", description = "내가 쓴 리뷰 리스트를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 쓴 리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<SliceResponse<MyReviewDetail>> getMyReviewList(
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) Long lastReviewId,
            @ParameterObject Pageable pageable,
            CustomUserDetails customUserDetails);

    @Operation(summary = "마이페이지 정보 조회", description = "마이페이지 정보를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "마이페이지 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<MyPageResponse> getMyPage(CustomUserDetails customUserDetails);

    @Operation(summary = "유저가 찜한 제휴 조회", description = "유저가 찜한 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 찜한 제휴 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<List<PartnershipResponse>> getUserLikedPartnerships(CustomUserDetails userDetails);

    @Operation(summary = "유저의 학과 등록", description = "유저의 학과를 등록하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저의 학과 등록 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 학과", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<?> registerDepartment(UpdateDepartmentRequest request, CustomUserDetails userDetails);

    @Operation(summary = "유저의 단과대/학과 제휴 조회", description = "유저의 단과대/학과 제휴를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저의 단과대/학과 제휴 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "유저의 학과 정보가 등록되지 않음", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<List<PartnershipResponse>> getUserDepartmentPartnerships(CustomUserDetails userDetails);

    @Operation(summary = "유저의 학과 조회", description = "유저의 학과를 조회하는 API 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저의 학과 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
            @ApiResponse(responseCode = "400", description = "유저의 학과 정보가 등록되지 않음", content = @Content(schema =
            @Schema(implementation = BaseResponse.class))),
    })
    BaseResponse<DepartmentResponse> getDepartment(CustomUserDetails userDetails);

    @Operation(summary = "내가 쓴 리뷰 리스트 조회", description = "내가 쓴 리뷰 리스트를 조회하는 API V2 입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내가 쓴 리뷰 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 유저", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<SliceResponse<MyMealReviewResponse>> getMyReviews(
            @Parameter(description = "마지막으로 조회된 reviewId값(첫 조회시 값 필요 없음)", in = ParameterIn.QUERY) Long lastReviewId,
            @ParameterObject Pageable pageable,
            CustomUserDetails customUserDetails);

    @Operation(summary = "단과대 조회", description = "숭실대학교 단과대학 들을 조회하는 API입니다.(토큰 불필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단과대 리스트 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단과대", content = @Content(schema = @Schema(implementation = BaseResponse.class)))
    })
    BaseResponse<List<GetCollegeResponse>> getColleges(CustomUserDetails userDetails);

    @Operation(summary = "단과대에 따른 학과 조회", description = "단과대학을 입력하면 단과대에 속한 숭실대학교 학과를 조회하는 API입니다.(토큰 불필요)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단과대 리스트 조회 성공"),
    })
    BaseResponse<List<GetDepartmentResponse>> getDepartments(Long collegeId, CustomUserDetails userDetails);
}
