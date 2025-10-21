package com.coursehub.course_service.security;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER, ROLE_INSTRUCTOR, ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
