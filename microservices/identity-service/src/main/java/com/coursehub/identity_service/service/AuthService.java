package com.coursehub.identity_service.service;

import com.coursehub.commons.security.model.*;
import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.common.TokenResponse;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.model.User;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

public interface AuthService {

    void register(CreateUserRequest request);

    TokenResponse login(LoginRequest request, HttpServletResponse response);

    void logout(HttpServletResponse response);

    TokenResponse refresh(RefreshTokenRequest request, HttpServletResponse response);

    void deleteSelf(UserPrincipal principal);

    void getVerifyMail(UserPrincipal principal);

    void verify( String activationCode);

    UserSelfResponse becameInstructor(UserPrincipal principal);

    void updateSelfPrincipals(UserPrincipal principal, UpdateSelfPrincipalsRequest request);

    void resetPassword(UserPrincipal principal, UpdateUserPasswordRequest request);

    void suspendSelf(UserPrincipal principal);

    void sendResetPasswordCode(SendResetPasswordCodeRequest request);

    void resetForgottenPassword(ForgottenPasswordResetRequest request, HttpServletResponse response);

    User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles);

}
