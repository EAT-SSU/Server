package ssu.eatssu.domain.partnership.presentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.partnership.service.PartnershipService;
import ssu.eatssu.domain.slack.service.SlackErrorNotifier;
import ssu.eatssu.domain.user.entity.Role;
import ssu.eatssu.global.handler.GlobalExceptionHandler;
import ssu.eatssu.global.handler.response.BaseException;
import ssu.eatssu.global.handler.response.BaseResponseStatus;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PartnershipControllerTest {

    @Mock
    private PartnershipService partnershipService;

    @Mock
    private SlackErrorNotifier slackErrorNotifier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new ParameterNamesModule())
                                                       .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
        mockMvc = MockMvcBuilders.standaloneSetup(new PartnershipController(partnershipService))
                                 .setControllerAdvice(new GlobalExceptionHandler(slackErrorNotifier))
                                 .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                                 .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                                 .build();
    }

    @Test
    void createPartnershipRegistersPartnership() throws Exception {
        mockMvc.perform(post("/partnerships")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"storeId\":1,\"college\":\"IT대\",\"department\":\"컴퓨터학부\","
                                                 + "\"description\":\"10% 할인\",\"startDate\":\"2026-03-01\","
                                                 + "\"endDate\":\"2026-07-30\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(partnershipService).createPartnership(any());
    }

    @Test
    void createPartnershipReturnsNotFoundWhenCollegeDoesNotExist() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_FOUND_COLLEGE)).when(partnershipService).createPartnership(any());

        mockMvc.perform(post("/partnerships")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"storeId\":1,\"college\":\"IT대\",\"department\":\"컴퓨터학부\","
                                                 + "\"description\":\"10% 할인\",\"startDate\":\"2026-03-01\","
                                                 + "\"endDate\":\"2026-07-30\"}"))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }

    @Test
    void getAllPartnershipsReturnsListForLoggedInUser() throws Exception {
        when(partnershipService.getAllPartnerships(any())).thenReturn(List.of());

        mockMvc.perform(get("/partnerships").with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result").isArray());
    }

    @Test
    void togglePartnershipLikeTogglesLikeForLoggedInUser() throws Exception {
        mockMvc.perform(post("/partnerships/{partnershipId}/like", 1L)
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true));

        verify(partnershipService).togglePartnershipLike(eq(1L), any());
    }

    @Test
    void togglePartnershipLikeReturnsNotFoundWhenPartnershipDoesNotExist() throws Exception {
        doThrow(new BaseException(BaseResponseStatus.NOT_FOUND_PARTNERSHIP))
                .when(partnershipService).togglePartnershipLike(eq(1L), any());

        mockMvc.perform(post("/partnerships/{partnershipId}/like", 1L)
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isNotFound())
               .andExpect(jsonPath("$.isSuccess").value(false));
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
