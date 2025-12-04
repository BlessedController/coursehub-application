package com.coursehub.identity_service.service;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.dto.response.admin.AdminUserResponse;
import com.coursehub.identity_service.dto.response.common.PageResponse;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.Gender;

import java.time.LocalDateTime;

public interface AdminService {
    User getUserById(String userId);

    void changeUserRole(String userId, UserRole role);

    void changeUserStatus(String userId, UserStatus status);

    PageResponse<AdminUserResponse> filterUsers(int page,
                                                int size,
                                                String keyword,
                                                UserRole role, UserStatus status,
                                                Gender gender, Boolean isVerified,
                                                LocalDateTime minDate,
                                                LocalDateTime maxDate,
                                                Double rating,
                                                String sortBy,
                                                String orderBy);


}
