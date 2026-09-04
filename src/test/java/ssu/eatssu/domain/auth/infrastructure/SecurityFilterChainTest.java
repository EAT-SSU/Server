package ssu.eatssu.domain.auth.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityFilterChainTest {

    @Autowired
    private MockMvc mockMvc;

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
        mockMvc.perform(get("/admin/anything")
                                .with(user("user").authorities(new SimpleGrantedAuthority("ROLE_USER"))))
               .andExpect(status().isForbidden());
    }

    @Test
    void 관리자_경로는_ADMIN_권한이_있으면_시큐리티를_통과한다() throws Exception {
        mockMvc.perform(get("/admin/anything")
                                .with(user("admin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
               .andExpect(status().isNotFound());
    }
}
