package ssu.eatssu.domain.notification.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.menu.entity.constants.TimePart;
import ssu.eatssu.domain.notification.dto.response.NotificationResponse;
import ssu.eatssu.domain.notification.dto.response.UnreadNotificationResponse;
import ssu.eatssu.domain.notification.entity.NotificationType;
import ssu.eatssu.domain.notification.service.NotificationService;
import ssu.eatssu.domain.user.entity.Role;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    @Mock
    private NotificationService notificationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new NotificationController(notificationService))
                                 .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                                 .build();
    }

    @Test
    void getNotificationsReturnsList() throws Exception {
        // given
        when(notificationService.getNotifications(any())).thenReturn(List.of(
                new NotificationResponse(1L, NotificationType.MENU_FAVORITE, "🍽️ 찜한 메뉴가 나온대요!",
                                         "오늘 점심, 학생식당에 돈까스 등장 👀", "20261102", TimePart.LUNCH, false,
                                         LocalDateTime.of(2026, 11, 2, 11, 0))));

        // when & then
        mockMvc.perform(get("/notifications").with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result[0].notificationId").value(1))
               .andExpect(jsonPath("$.result[0].date").value("20261102"))
               .andExpect(jsonPath("$.result[0].timePart").value("LUNCH"))
               .andExpect(jsonPath("$.result[0].isRead").value(false));
    }

    @Test
    void hasUnreadReturnsFlag() throws Exception {
        // given
        when(notificationService.hasUnread(any()))
                .thenReturn(new UnreadNotificationResponse(true));

        // when & then
        mockMvc.perform(get("/notifications/unread").with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.hasUnread").value(true));
    }

    @Test
    void readDelegatesToService() throws Exception {
        // when
        mockMvc.perform(patch("/notifications/5/read").with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk());

        // then
        verify(notificationService).read(any(), eq(5L));
    }

    @Test
    void readAllDelegatesToService() throws Exception {
        // when
        mockMvc.perform(patch("/notifications/read-all").with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk());

        // then
        verify(notificationService).readAll(any());
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
