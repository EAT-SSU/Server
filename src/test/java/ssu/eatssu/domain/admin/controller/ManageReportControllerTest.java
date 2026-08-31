package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.admin.dto.response.PageWrapper;
import ssu.eatssu.domain.admin.dto.response.ReportLine;
import ssu.eatssu.domain.admin.service.ManageReportService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageReportControllerTest {

    @Mock
    private ManageReportService manageReportService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ManageReportController(manageReportService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                                 .build();
    }

    @Test
    void returnsReportBoardPage() throws Exception {
        PageWrapper<ReportLine> page = new PageWrapper<>(List.of(), 0, 0L, 0, 20, 0, true, true);
        when(manageReportService.getReportBoard(any())).thenReturn(page);

        mockMvc.perform(get("/admin/reports"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result.totalElements").value(0));

        verify(manageReportService).getReportBoard(eq(PageRequest.of(0, 20)));
    }

    @Test
    void deletesReportById() throws Exception {
        mockMvc.perform(delete("/admin/reports/{reportId}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(manageReportService).delete(eq(1L));
    }

    @Test
    void returnsErrorStatusWhenDeleteFails() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_FOUND_REVIEW_REPORT))
                .when(manageReportService).delete(eq(1L));

        mockMvc.perform(delete("/admin/reports/{reportId}", 1L))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
