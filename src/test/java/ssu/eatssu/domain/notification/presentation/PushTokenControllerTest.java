package ssu.eatssu.domain.notification.presentation;

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
import ssu.eatssu.domain.notification.dto.request.DeletePushTokenRequest;
import ssu.eatssu.domain.notification.dto.request.PushTokenRequest;
import ssu.eatssu.domain.notification.service.PushTokenService;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PushTokenControllerTest {

    @Mock
    private PushTokenService pushTokenService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new PushTokenController(pushTokenService))
                                 .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                                 .build();
    }

    @Test
    void registerTokenDelegatesToService() throws Exception {
        // when
        mockMvc.perform(put("/users/push-token")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"token\",\"deviceType\":\"IOS\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        // then
        verify(pushTokenService).registerToken(any(), eq(new PushTokenRequest("token", DeviceType.IOS)));
    }

    @Test
    void registerTokenRejectsBlankToken() throws Exception {
        // when
        mockMvc.perform(put("/users/push-token")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\" \",\"deviceType\":\"IOS\"}"))
               .andExpect(status().isBadRequest());

        // then
        verifyNoInteractions(pushTokenService);
    }

    @Test
    void deleteTokenDelegatesToService() throws Exception {
        // when
        mockMvc.perform(delete("/users/push-token")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails()))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"token\"}"))
               .andExpect(status().isOk());

        // then
        verify(pushTokenService).deleteToken(any(), eq(new DeletePushTokenRequest("token")));
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
