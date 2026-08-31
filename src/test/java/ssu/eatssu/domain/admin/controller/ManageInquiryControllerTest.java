package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.admin.dto.request.UpdateStatusRequest;
import ssu.eatssu.domain.admin.dto.response.InquiryLine;
import ssu.eatssu.domain.admin.dto.response.PageWrapper;
import ssu.eatssu.domain.admin.service.ManageInquiryService;
import ssu.eatssu.domain.inquiry.entity.InquiryStatus;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.GlobalExceptionHandler;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageInquiryControllerTest {

    @Mock
    private ManageInquiryService manageInquiryService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ManageInquiryController(manageInquiryService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                                 .build();
    }

    @Test
    void returnsInquiryBoardPage() throws Exception {
        PageWrapper<InquiryLine> page = new PageWrapper<>(List.of(), 0, 0L, 0, 20, 0, true, true);
        when(manageInquiryService.getInquiryBoard(any())).thenReturn(page);

        mockMvc.perform(get("/admin/inquiries"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.totalElements").value(0));

        verify(manageInquiryService).getInquiryBoard(eq(PageRequest.of(0, 20)));
    }

    @Test
    void updatesInquiryStatus() throws Exception {
        mockMvc.perform(patch("/admin/inquiries/{inquiryId}/status", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"ANSWERED\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageInquiryService).updateStatus(eq(1L), eq(new UpdateStatusRequest(InquiryStatus.ANSWERED)));
    }

    @Test
    void returnsNotFoundWhenInquiryDoesNotExist() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_FOUND_USER_INQUIRY))
                .when(manageInquiryService).updateStatus(eq(1L), eq(new UpdateStatusRequest(InquiryStatus.ANSWERED)));

        mockMvc.perform(patch("/admin/inquiries/{inquiryId}/status", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"status\":\"ANSWERED\"}"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void deletesInquiryById() throws Exception {
        mockMvc.perform(delete("/admin/inquiries/{inquiryId}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageInquiryService).delete(eq(1L));
    }
}
