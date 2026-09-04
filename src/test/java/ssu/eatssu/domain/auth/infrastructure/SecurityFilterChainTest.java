package ssu.eatssu.domain.auth.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.auth.security.JwtTokenProvider;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String accessTokenWithRole(Role role) {
        CustomUserDetails principal = new CustomUserDetails(1L, "test@eatssu.com", "", role, DeviceType.IOS);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        return jwtTokenProvider.generateTokens(authentication).accessToken();
    }

    @Test
    void 화이트리스트_경로는_인증_없이_시큐리티를_통과한다() throws Exception {
        mockMvc.perform(get("/menus"))
               .andExpect(status().isBadRequest());
    }

    @Test
    void 화이트리스트에_없는_경로는_인증하지_않으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/protected-test-path"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void 관리자_경로는_인증하지_않으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/admin/anything"))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void 관리자_경로는_ADMIN_권한이_없으면_403을_반환한다() throws Exception {
        String token = accessTokenWithRole(Role.USER);

        mockMvc.perform(get("/admin/anything").header("Authorization", "Bearer " + token))
               .andExpect(status().isForbidden());
    }

    @Test
    void 관리자_경로는_ADMIN_권한이_있으면_시큐리티를_통과한다() throws Exception {
        String token = accessTokenWithRole(Role.ADMIN);

        mockMvc.perform(get("/admin/anything").header("Authorization", "Bearer " + token))
               .andExpect(status().isNotFound());
    }
}
