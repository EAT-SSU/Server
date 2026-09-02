package ssu.eatssu.domain.user.presentation;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.dto.response.PartnershipResponse;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.domain.review.service.ReviewServiceV2;
import ssu.eatssu.domain.slice.dto.SliceResponse;
import ssu.eatssu.domain.slice.service.SliceService;
import ssu.eatssu.domain.user.dto.request.LanguageUpdateRequest;
import ssu.eatssu.domain.user.dto.request.NicknameUpdateRequest;
import ssu.eatssu.domain.user.dto.request.UpdateDepartmentRequest;
import ssu.eatssu.domain.user.dto.response.DepartmentResponse;
import ssu.eatssu.domain.user.dto.response.GetCollegeResponse;
import ssu.eatssu.domain.user.dto.response.GetDepartmentResponse;
import ssu.eatssu.domain.user.dto.response.LanguageResponse;
import ssu.eatssu.domain.user.dto.response.MyMealReviewResponse;
import ssu.eatssu.domain.user.dto.response.MyPageResponse;
import ssu.eatssu.domain.user.dto.response.MyReviewDetail;
import ssu.eatssu.domain.user.entity.Language;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.service.UserService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private final UserService userService = mock(UserService.class);
    private final SliceService sliceService = mock(SliceService.class);
    private final PartnershipService partnershipService = mock(PartnershipService.class);
    private final ReviewServiceV2 reviewServiceV2 = mock(ReviewServiceV2.class);
    private final UserController controller = new UserController(userService, sliceService, partnershipService,
            reviewServiceV2);
    private final CustomUserDetails userDetails = new CustomUserDetails(1L, "user@eatssu.com", "credentials",
            Role.USER, null);

    @Test
    void 이메일_중복_여부를_반환한다() {
        when(userService.validateDuplicatedEmail("user@eatssu.com")).thenReturn(true);

        assertThat(controller.validateDuplicatedEmail("user@eatssu.com").getResult()).isTrue();
    }

    @Test
    void 닉네임_사용_가능_여부를_반환한다() {
        when(userService.validateNickname("잇슈")).thenReturn(true);

        assertThat(controller.validateNickname("잇슈").getResult()).isTrue();
    }

    @Test
    void 닉네임을_수정한다() {
        NicknameUpdateRequest request = new NicknameUpdateRequest("새닉네임");

        controller.updateNickname(request, userDetails);

        verify(userService).updateNickname(userDetails, request);
    }

    @Test
    void 언어를_수정한다() {
        LanguageUpdateRequest request = new LanguageUpdateRequest(Language.EN);

        controller.updateLanguage(request, userDetails);

        verify(userService).updateLanguage(userDetails, request);
    }

    @Test
    void 언어_설정을_조회한다() {
        LanguageResponse response = new LanguageResponse(Language.JA);
        when(userService.findLanguage(userDetails)).thenReturn(response);

        assertThat(controller.getLanguage(userDetails).getResult()).isEqualTo(response);
    }

    @Test
    void 회원_탈퇴를_수행한다() {
        when(userService.withdraw(userDetails)).thenReturn(true);

        assertThat(controller.withdraw(userDetails).getResult()).isTrue();
    }

    @Test
    void 내_리뷰_목록을_조회한다() {
        SliceResponse<MyReviewDetail> sliceResponse = SliceResponse.<MyReviewDetail>builder()
                .dataList(List.of()).numberOfElements(0).hasNext(false).build();
        when(sliceService.findMyReviews(eq(userDetails), any(), eq(5L))).thenReturn(sliceResponse);

        var result = controller.getMyReviewList(5L, PageRequest.of(0, 20), userDetails);

        assertThat(result.getResult()).isSameAs(sliceResponse);
    }

    @Test
    void 마이페이지_정보를_조회한다() {
        MyPageResponse response = MyPageResponse.builder().nickname("닉네임").build();
        when(userService.findMyPage(userDetails)).thenReturn(response);

        assertThat(controller.getMyPage(userDetails).getResult()).isSameAs(response);
    }

    @Test
    void 좋아요한_제휴업체_목록을_조회한다() {
        List<PartnershipResponse> responses = List.of();
        when(partnershipService.getUserLikedPartnerships(userDetails)).thenReturn(responses);

        assertThat(controller.getUserLikedPartnerships(userDetails).getResult()).isSameAs(responses);
    }

    @Test
    void 학과를_등록한다() {
        UpdateDepartmentRequest request = new UpdateDepartmentRequest(3L);

        controller.registerDepartment(request, userDetails);

        verify(userService).registerDepartment(request, userDetails);
    }

    @Test
    void 학과_제휴업체_목록을_조회한다() {
        List<PartnershipResponse> responses = List.of();
        when(partnershipService.getUserDepartmentPartnerships(userDetails)).thenReturn(responses);

        assertThat(controller.getUserDepartmentPartnerships(userDetails).getResult()).isSameAs(responses);
    }

    @Test
    void 학과_정보를_조회한다() {
        DepartmentResponse response = new DepartmentResponse(1L, "컴퓨터학부", 2L, "IT대학");
        when(userService.getDepartment(userDetails)).thenReturn(response);

        assertThat(controller.getDepartment(userDetails).getResult()).isEqualTo(response);
    }

    @Test
    void 내_식단_리뷰_목록을_V2로_조회한다() {
        SliceResponse<MyMealReviewResponse> sliceResponse = SliceResponse.<MyMealReviewResponse>builder()
                .dataList(List.of()).numberOfElements(0).hasNext(false).build();
        when(reviewServiceV2.findMyReviews(eq(userDetails), eq(5L), any())).thenReturn(sliceResponse);

        var result = controller.getMyReviews(5L, PageRequest.of(0, 20), userDetails);

        assertThat(result.getResult()).isSameAs(sliceResponse);
    }

    @Test
    void 단과대_목록을_조회한다() {
        List<GetCollegeResponse> responses = List.of(GetCollegeResponse.builder().id(1L).name("IT대학").build());
        when(userService.getCollegeList(userDetails)).thenReturn(responses);

        assertThat(controller.getColleges(userDetails).getResult()).isSameAs(responses);
    }

    @Test
    void 학과_목록을_조회한다() {
        List<GetDepartmentResponse> responses = List.of(GetDepartmentResponse.builder().id(1L).name("컴퓨터학부").build());
        when(userService.getDepartmentList(1L, userDetails)).thenReturn(responses);

        assertThat(controller.getDepartments(1L, userDetails).getResult()).isSameAs(responses);
    }
}
