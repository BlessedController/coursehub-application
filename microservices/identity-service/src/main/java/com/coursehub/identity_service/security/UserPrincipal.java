package com.coursehub.identity_service.security;

import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.UserRole;
import com.coursehub.identity_service.model.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@Setter
public class UserPrincipal implements UserDetails {
    private String id;
    private String username;
    private String email;
    private UserRole userRole;
    private String password;
    private UserStatus userStatus;
    private Boolean isVerified;

    public UserPrincipal(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.userRole = user.getUserRole();
        this.userStatus = user.getUserStatus();
        this.isVerified = user.getIsVerified();
    }

    public UserPrincipal(String userId, String username, String email, String role) {
        this.id = userId;
        this.username = username;
        this.email = email;
        this.userRole = UserRole.valueOf(role);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(this.userRole);
    }

}
