package com.coursehub.identity_service.service.abstracts;

import com.coursehub.commons.security.*;
import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.common.TokenResponse;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.model.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

public interface AuthService {

    void register(CreateUserRequest request);

    TokenResponse authenticate(LoginRequest request, HttpServletResponse response);

    void logout(HttpServletResponse response);

    TokenResponse refresh(RefreshTokenRequest request, HttpServletResponse response);

    void deleteSelf(UserPrincipal principal);

    void getVerifyMail(UserPrincipal principal);

    void verify(UserPrincipal principal, String activationCode);

    UserSelfResponse becameInstructor(UserPrincipal principal);

    void updateSelfPrincipals(UserPrincipal principal, UpdateSelfPrincipalsRequest request);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    void updateSelfPassword(UserPrincipal principal, UpdateUserPasswordRequest request);

    void inactivateSelf(UserPrincipal principal);

    Boolean isContentCreatorExist(String instructorId);

    User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles);

}
