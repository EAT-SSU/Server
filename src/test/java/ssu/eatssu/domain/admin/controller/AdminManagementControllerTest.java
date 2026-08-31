package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ssu.eatssu.domain.admin.dto.request.LoginRequest;
import ssu.eatssu.domain.admin.dto.request.RegisterFixMenuRequest;
import ssu.eatssu.domain.admin.dto.request.RegisterMealRequest;
import ssu.eatssu.domain.admin.dto.request.UpdateFixMenuRequest;
import ssu.eatssu.domain.admin.dto.request.UpdateStatusRequest;
import ssu.eatssu.domain.admin.service.AuthenticationService;
import ssu.eatssu.domain.admin.service.ManageFixMenuService;
import ssu.eatssu.domain.admin.service.ManageInquiryService;
import ssu.eatssu.domain.admin.service.ManageMealService;
import ssu.eatssu.domain.admin.service.ManageReportService;
import ssu.eatssu.domain.admin.service.ManageReviewService;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.restaurant.entity.Restaurant;
import ssu.eatssu.domain.user.dto.response.Tokens;

import java.util.ArrayList;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AdminManagementControllerTest {

    @Test
    void authenticationControllerReturnsTokens() {
        AuthenticationService service = mock(AuthenticationService.class);
        Tokens tokens = new Tokens("access", "refresh");
        given(service.login(new LoginRequest("admin", "password"))).willReturn(tokens);

        assertThat(new AuthenticationController(service).login(new LoginRequest("admin", "password")).getResult())
                .isSameAs(tokens);
    }

    @Test
    void fixMenuControllerDelegatesManagementActions() {
        ManageFixMenuService service = mock(ManageFixMenuService.class);
        given(service.changeDiscontinuedStatus(1L)).willReturn(true);
        ManageFixMenuController controller = new ManageFixMenuController(service);
        RegisterFixMenuRequest registerRequest = new RegisterFixMenuRequest(1L, "돈가스", 6000);
        UpdateFixMenuRequest updateRequest = new UpdateFixMenuRequest("치즈돈가스", 7000);

        assertThat(controller.register(Restaurant.FOOD_COURT, registerRequest).getIsSuccess()).isTrue();
        assertThat(controller.update(1L, updateRequest).getIsSuccess()).isTrue();
        assertThat(controller.delete(1L).getIsSuccess()).isTrue();
        assertThat(controller.toggleDiscontinuedStatus(1L).getResult()).isTrue();
        controller.fixMenuPage();

        verify(service).register(Restaurant.FOOD_COURT, registerRequest);
        verify(service).updateMenu(1L, updateRequest);
        verify(service).delete(1L);
        verify(service).getMenuBoards();
    }

    @Test
    void inquiryControllerDelegatesStatusAndDelete() {
        ManageInquiryService service = mock(ManageInquiryService.class);
        ManageInquiryController controller = new ManageInquiryController(service);
        UpdateStatusRequest request = new UpdateStatusRequest(InquiryStatus.ANSWERED);

        assertThat(controller.inquiryPage(PageRequest.of(0, 20), null).getIsSuccess()).isTrue();
        assertThat(controller.updateStatus(1L, request).getIsSuccess()).isTrue();
        assertThat(controller.delete(1L).getIsSuccess()).isTrue();

        verify(service).getInquiryBoard(PageRequest.of(0, 20));
        verify(service).updateStatus(1L, request);
        verify(service).delete(1L);
    }

    @Test
    void mealControllerDelegatesRegisterAndDelete() {
        ManageMealService service = mock(ManageMealService.class);
        ManageMealController controller = new ManageMealController(service);
        Date date = new Date();
        RegisterMealRequest request = new RegisterMealRequest(new ArrayList<>(java.util.List.of("라면")));

        assertThat(controller.mealPage(date, TimePart.LUNCH, null).getIsSuccess()).isTrue();
        assertThat(controller.register(Restaurant.DODAM, date, TimePart.LUNCH, request, null).getIsSuccess()).isTrue();
        assertThat(controller.delete(1L).getIsSuccess()).isTrue();

        verify(service).getMenuBoards(date, TimePart.LUNCH);
        verify(service).delete(1L);
    }

    @Test
    void reportAndReviewControllersDelegateDelete() {
        ManageReportService reportService = mock(ManageReportService.class);
        ManageReviewService reviewService = mock(ManageReviewService.class);
        ManageReportController reportController = new ManageReportController(reportService);
        ManageReviewController reviewController = new ManageReviewController(reviewService);

        assertThat(reportController.reportPage(PageRequest.of(0, 20), null).getIsSuccess()).isTrue();
        assertThat(reportController.delete(1L).getIsSuccess()).isTrue();
        assertThat(reviewController.delete(2L).getIsSuccess()).isTrue();

        verify(reportService).getReportBoard(PageRequest.of(0, 20));
        verify(reportService).delete(1L);
        verify(reviewService).delete(2L);
    }
}
