package ssu.eatssu.domain.user.presentation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import ssu.eatssu.domain.partnership.dto.response.PartnershipResponse;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.slice.service.SliceService;
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
import ssu.eatssu.domain.user.presentation.docs.UserControllerDocs;
import ssu.eatssu.domain.user.service.UserService;
import ssu.eatssu.global.handler.response.BaseResponse;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController implements UserControllerDocs {

    private final UserService userService;
    private final SliceService sliceService;
    private final PartnershipService partnershipService;
    private final ReviewServiceV2 reviewServiceV2;

    @Override
    @PostMapping("/validate/email/{email}") //todo: 중복인 경우 error throw, 중복 아니면 ApiReposne return
    public BaseResponse<Boolean> validateDuplicatedEmail(
            @PathVariable String email) {
        return BaseResponse.success(userService.validateDuplicatedEmail(email));
    }

    @Override
    @GetMapping("/validate/nickname")
    public BaseResponse<Boolean> validateNickname(@RequestParam(value = "nickname") String nickname) {
        return BaseResponse.success(userService.validateNickname(nickname));
    }

    @Override
    @PatchMapping("/nickname")
    public BaseResponse<?> updateNickname(
            @Valid @RequestBody NicknameUpdateRequest updateNicknameRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateNickname(userDetails, updateNicknameRequest);
        return BaseResponse.success();
    }

    @Override
    @PatchMapping("/language")
    public BaseResponse<?> updateLanguage(
            @Valid @RequestBody LanguageUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.updateLanguage(userDetails, request);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("/language")
    public BaseResponse<LanguageResponse> getLanguage(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.findLanguage(userDetails));
    }

    @Override
    @DeleteMapping("")
    public BaseResponse<Boolean> withdraw(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.withdraw(userDetails));
    }

    @Override
    @GetMapping("/reviews")
    public BaseResponse<SliceResponse<MyReviewDetail>> getMyReviewList(
            @RequestParam(required = false) Long lastReviewId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<MyReviewDetail> myReviews = sliceService.findMyReviews(customUserDetails,
                                                                             pageable,
                                                                             lastReviewId);
        return BaseResponse.success(myReviews);
    }


    @Override
    @GetMapping("/mypage")
    public BaseResponse<MyPageResponse> getMyPage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return BaseResponse.success(userService.findMyPage(customUserDetails));
    }

    @Override
    @GetMapping("/partnerships")
    public BaseResponse<List<PartnershipResponse>> getUserLikedPartnerships(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getUserLikedPartnerships(userDetails));
    }

    @Override
    @PostMapping("/department")
    public BaseResponse<?> registerDepartment(@RequestBody UpdateDepartmentRequest request,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.registerDepartment(request, userDetails);
        return BaseResponse.success();
    }

    @Override
    @GetMapping("/department/partnerships")
    public BaseResponse<List<PartnershipResponse>> getUserDepartmentPartnerships(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(partnershipService.getUserDepartmentPartnerships(userDetails));
    }


    @Override
    @GetMapping("/department")
    public BaseResponse<DepartmentResponse> getDepartment(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return BaseResponse.success(userService.getDepartment(userDetails));
    }

    @Override
    @GetMapping("/v2/reviews")
    public BaseResponse<SliceResponse<MyMealReviewResponse>> getMyReviews(
            @RequestParam(required = false) Long lastReviewId,
            @PageableDefault(size = 20, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        SliceResponse<MyMealReviewResponse> myReviews = reviewServiceV2.findMyReviews(customUserDetails,
                                                                                      lastReviewId,
                                                                                      pageable);
        return BaseResponse.success(myReviews);
    }

    @Override
    @GetMapping("/lookup/colleges")
    public BaseResponse<List<GetCollegeResponse>> getColleges(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GetCollegeResponse> getCollegeResponses = userService.getCollegeList(userDetails);
        return BaseResponse.success(getCollegeResponses);
    }

    @Override
    @GetMapping("/lookup/departments")
    public BaseResponse<List<GetDepartmentResponse>> getDepartments(@RequestParam Long collegeId,
                                                                    @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<GetDepartmentResponse> getCollegeResponses = userService.getDepartmentList(collegeId, userDetails);
        return BaseResponse.success(getCollegeResponses);
    }

}
