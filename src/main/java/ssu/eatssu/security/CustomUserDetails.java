package ssu.eatssu.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ssu.eatssu.domain.Role;
import ssu.eatssu.domain.User;

import java.util.ArrayList;
import java.util.Collection;

public class CustomUserDetails implements UserDetails {
    //private final Long userId;
    private final String email;
    private final String pwd;
    private final Role role;

    public CustomUserDetails(User user){
        //this.userId = user.getId();
        this.email = user.getEmail();
        this.pwd = user.getPwd();
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
        return pwd;
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
