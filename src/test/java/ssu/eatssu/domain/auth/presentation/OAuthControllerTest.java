package ssu.eatssu.domain.auth.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.auth.dto.request.ValidRequest;
import ssu.eatssu.domain.auth.service.OAuthService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OAuthControllerTest {

    @Mock
    private OAuthService oauthService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new OAuthController(oauthService)).build();
    }

    @Test
    void 토큰_유효성을_반환한다() throws Exception {
        when(oauthService.validToken(new ValidRequest("valid-token"))).thenReturn(true);

        mockMvc.perform(post("/oauths/valid/token")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"token\":\"valid-token\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isSuccess").value(true))
               .andExpect(jsonPath("$.result").value(true));

        verify(oauthService).validToken(new ValidRequest("valid-token"));
    }
}
