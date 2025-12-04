package com.coursehub.commons.security.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {
    private final String id;
    private final String username;
    private final String email;
    private final UserRole role;
    private final String password;

    public UserPrincipal(String userId, String username, String email, UserRole role, String password) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.role = role;
        this.password = password;
    }

    public UserPrincipal(String userId, String username, String email, String role) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.role = UserRole.valueOf(role);
        this.password = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }


}
