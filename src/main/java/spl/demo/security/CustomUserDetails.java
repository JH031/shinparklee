package spl.demo.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spl.demo.entity.SignupEntity;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final SignupEntity user;

    public CustomUserDetails(SignupEntity user) {
        this.user = user;
    }

    // ✅ getUser() 메서드 추가!
    public SignupEntity getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserId();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
