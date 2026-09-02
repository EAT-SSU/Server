package ssu.eatssu.domain.inquiry.presentation;

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
import ssu.eatssu.domain.inquiry.entity.Inquiry;
import ssu.eatssu.domain.inquiry.service.InquiryService;
import ssu.eatssu.domain.slack.entity.SlackChannel;
import ssu.eatssu.domain.slack.service.SlackService;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.domain.user.entity.User;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InquiryControllerTest {

    @Mock
    private SlackService slackService;

    @Mock
    private InquiryService inquiryService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new InquiryController(slackService, inquiryService))
                                 .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                                 .build();
    }

    @Test
    void writesInquiryAndNotifiesSlack() throws Exception {
        User writer = mock(User.class);
        when(writer.getId()).thenReturn(1L);
        when(writer.getNickname()).thenReturn("문의자");
        when(writer.getEmail()).thenReturn("test@test.com");

        Inquiry inquiry = mock(Inquiry.class);
        when(inquiry.getUser()).thenReturn(writer);
        when(inquiry.getContent()).thenReturn("문의합니다");
        when(inquiry.getCreatedDate()).thenReturn(LocalDateTime.now());
        when(inquiryService.createUserInquiry(any(), any())).thenReturn(inquiry);

        mockMvc.perform(post("/inquiries/")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"test@test.com\",\"content\":\"문의합니다\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(slackService).sendSlackMessage(any(String.class), eq(SlackChannel.USER_INQUIRY_CHANNEL));
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
