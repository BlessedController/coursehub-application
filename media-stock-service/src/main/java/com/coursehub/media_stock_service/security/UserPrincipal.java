package com.coursehub.media_stock_service.security;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserPrincipal implements UserDetails {
    private String id;
    private String username;
    private String password;
    private String email;
    private UserRole role;

    public UserPrincipal(String userId, String username, String email, String role) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.role = UserRole.valueOf(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

}
