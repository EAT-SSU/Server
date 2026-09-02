package ssu.eatssu.domain.admin.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.admin.service.ManageReviewService;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.global.handler.GlobalExceptionHandler;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ManageReviewControllerTest {

    @Mock
    private ManageReviewService manageReviewService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ManageReviewController(manageReviewService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .build();
    }

    @Test
    void deletesReviewById() throws Exception {
        mockMvc.perform(delete("/admin/reviews/{reviewId}", 1L))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.code").value(1000));

        verify(manageReviewService).delete(eq(1L));
    }

    @Test
    void returnsErrorStatusWhenServiceThrows() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_FOUND_REVIEW))
                .when(manageReviewService).delete(eq(1L));

        mockMvc.perform(delete("/admin/reviews/{reviewId}", 1L))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }
}
