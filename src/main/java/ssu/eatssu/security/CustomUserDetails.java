package ssu.eatssu.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ssu.eatssu.domain.user.User;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String email;
    private final String credentials;
    private final GrantedAuthority role;

    public CustomUserDetails(User user){
        this.id = user.getId();
        this.email = user.getEmail();
        this.credentials = user.getCredentials();
        this.role = user.getRole();
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.getAuthority()));
        return authorities;
    }

    @Override
    public String getPassword() {
        return credentials;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
