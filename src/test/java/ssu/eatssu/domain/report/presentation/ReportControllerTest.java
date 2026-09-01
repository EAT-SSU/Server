package ssu.eatssu.domain.report.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.report.dto.response.ReportTypeList;
import ssu.eatssu.domain.report.entity.ReportType;
import ssu.eatssu.domain.report.service.ReportService;
import ssu.eatssu.domain.review.entity.Report;
import ssu.eatssu.domain.review.entity.Review;
import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    private ReportService reportService;

    @Mock
    private SlackService slackService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ReportController(reportService, slackService))
                                 .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                                 .build();
    }

    @Test
    void 신고_유형을_반환한다() throws Exception {
        ReportTypeList types = ReportTypeList.get();
        when(reportService.getReportType()).thenReturn(types);

        mockMvc.perform(get("/reports/types"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void 리뷰를_신고하고_슬랙으로_알린다() throws Exception {
        Report report = mockReport();
        when(reportService.reportReview(any(), any())).thenReturn(report);

        mockMvc.perform(post("/reports")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"reviewId\":1,\"reportType\":\"NO_ASSOCIATE_CONTENT\",\"content\":\"신고내용\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(slackService).sendSlackMessage(any(String.class), any());
    }

    private Report mockReport() {
        User reporter = mock(User.class);
        when(reporter.getId()).thenReturn(1L);
        when(reporter.getNickname()).thenReturn("신고자");

        User writer = mock(User.class);
        when(writer.getId()).thenReturn(2L);
        when(writer.getNickname()).thenReturn("작성자");

        Review review = mock(Review.class);
        when(review.getId()).thenReturn(10L);
        when(review.getUser()).thenReturn(writer);
        when(review.getMenu()).thenReturn(null);
        when(review.getMeal()).thenReturn(null);
        when(review.getContent()).thenReturn("리뷰내용");
        when(review.getModifiedDate()).thenReturn(LocalDateTime.now());

        Report report = mock(Report.class);
        when(report.getUser()).thenReturn(reporter);
        when(report.getReview()).thenReturn(review);
        when(report.getReportType()).thenReturn(ReportType.NO_ASSOCIATE_CONTENT);
        when(report.getContent()).thenReturn("신고내용");
        when(report.getCreatedDate()).thenReturn(LocalDateTime.now());
        return report;
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
