package com.coursehub.commons.security.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER, ROLE_CONTENT_CREATOR, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
