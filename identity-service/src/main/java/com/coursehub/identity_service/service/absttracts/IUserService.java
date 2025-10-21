package com.coursehub.identity_service.service.absttracts;

import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.RatingMQResponseForIdentityService;
import com.coursehub.identity_service.dto.response.user.UserResponseForOthers;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.UserRole;
import com.coursehub.identity_service.model.enums.UserStatus;
import com.coursehub.identity_service.security.UserPrincipal;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Set;

public interface IUserService {

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    void register(CreateUserRequest request);

    UserResponseForOthers getUserById(String id);

    void updateSelfInfo(UserPrincipal principal, UpdateUserInfoRequest request);

    void updateSelfPrincipals(UserPrincipal principal, UpdateSelfPrincipalsRequest request);

    void updateSelfPassword(UserPrincipal principal, UpdateUserPasswordRequest request);

    void inactivateSelf(UserPrincipal principal);

    UserSelfResponse getSelf(UserPrincipal principal);

    void deleteSelf(UserPrincipal principal);

    String authenticate(LoginRequest request, HttpServletResponse response);

    void getVerifyMail(UserPrincipal principal);

    void verify(UserPrincipal principal, String activationCode);

    UserSelfResponse becameInstructor(UserPrincipal principal);

    Boolean isInstructorExist(String instructorId);

    void listenAddInstructorRating(RatingMQResponseForIdentityService response);

    void listenDeleteInstructorRating(RatingMQResponseForIdentityService response);

    User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles);
}
