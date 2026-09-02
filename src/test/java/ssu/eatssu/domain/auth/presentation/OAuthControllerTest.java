package ssu.eatssu.domain.auth.presentation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ssu.eatssu.domain.auth.dto.request.AppleLoginRequest;
import ssu.eatssu.domain.auth.dto.request.AppleLoginRequestV2;
import ssu.eatssu.domain.auth.dto.request.KakaoLoginRequest;
import ssu.eatssu.domain.auth.dto.request.KakaoLoginRequestV2;
import ssu.eatssu.domain.auth.dto.request.ValidRequest;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.auth.service.OAuthService;
import ssu.eatssu.domain.user.dto.response.Tokens;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;

import static org.mockito.ArgumentMatchers.any;
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

    @Test
    void 카카오_로그인을_처리한다() throws Exception {
        when(oauthService.kakaoLogin(new KakaoLoginRequest("test@test.com", "provider-1")))
                .thenReturn(new Tokens("access", "refresh"));

        mockMvc.perform(post("/oauths/kakao")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"test@test.com\",\"providerId\":\"provider-1\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.accessToken").value("access"));
    }

    @Test
    void 카카오_로그인_V2를_처리한다() throws Exception {
        when(oauthService.kakaoLoginV2(new KakaoLoginRequestV2("test@test.com", "provider-1", DeviceType.IOS)))
                .thenReturn(new Tokens("access", "refresh"));

        mockMvc.perform(post("/oauths/v2/kakao")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"test@test.com\",\"providerId\":\"provider-1\",\"deviceType\":\"IOS\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.accessToken").value("access"));
    }

    @Test
    void 애플_로그인을_처리한다() throws Exception {
        when(oauthService.appleLogin(new AppleLoginRequest("identity-token")))
                .thenReturn(new Tokens("access", "refresh"));

        mockMvc.perform(post("/oauths/apple")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"identityToken\":\"identity-token\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.accessToken").value("access"));
    }

    @Test
    void 애플_로그인_V2를_처리한다() throws Exception {
        when(oauthService.appleLoginV2(new AppleLoginRequestV2("identity-token", DeviceType.ANDROID)))
                .thenReturn(new Tokens("access", "refresh"));

        mockMvc.perform(post("/oauths/v2/apple")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"identityToken\":\"identity-token\",\"deviceType\":\"ANDROID\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.accessToken").value("access"));
    }

    @Test
    void 로그인된_사용자의_토큰을_재발급한다() throws Exception {
        when(oauthService.refreshTokens(any())).thenReturn(new Tokens("new-access", "new-refresh"));

        mockMvc.perform(post("/oauths/reissue/token")
                                .with(SecurityMockMvcRequestPostProcessors.user(userDetails())))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.result.accessToken").value("new-access"));
    }

    private CustomUserDetails userDetails() {
        return new CustomUserDetails(1L, "user@eatssu.com", "credentials", Role.USER, null);
    }
}
