package dev.kostya.ticketplatform.auth.security;

import dev.kostya.ticketplatform.auth.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public class UserPrincipal implements UserDetails {
    private final Long id;
    private final Role role;
    @Setter
    private UUID sessionId;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, Role role) {
        this(id, role, null);
    }

    public UserPrincipal(Long id, Role role, UUID sessionId) {
        this.id = id;
        this.role = role;
        this.sessionId = sessionId;
        this.authorities = List.of(
                new SimpleGrantedAuthority( role.name() )
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return "";
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
