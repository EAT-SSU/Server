package ssu.eatssu.domain.auth.infrastructure;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ssu.eatssu.domain.auth.security.CustomUserDetails;
import ssu.eatssu.domain.user.entity.DeviceType;
import ssu.eatssu.domain.user.entity.Role;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityUtilTest {

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void returnsPrincipalAndIdForAuthenticatedUser() {
        CustomUserDetails details = new CustomUserDetails(7L, "user@eatssu.com", "credentials", Role.USER,
                DeviceType.IOS);
        Authentication authentication = new UsernamePasswordAuthenticationToken(details, "token");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(SecurityUtil.getLoginUser()).isSameAs(authentication);
        assertThat(SecurityUtil.getLoginUserPrincipal().getId()).isEqualTo(7L);
        assertThat(SecurityUtil.getLoginUserPrincipal().getEmail()).isEqualTo("user@eatssu.com");
        assertThat(SecurityUtil.getLoginUserId()).isEqualTo(7L);
    }

    @Test
    void returnsNullPrincipalAndIdForNonUserDetailsAuthentication() {
        Authentication authentication = new UsernamePasswordAuthenticationToken("anonymous", null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        assertThat(SecurityUtil.getLoginUserPrincipal()).isNull();
        assertThat(SecurityUtil.getLoginUserId()).isNull();
    }
}
