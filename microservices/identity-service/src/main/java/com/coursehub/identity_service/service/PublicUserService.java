package com.coursehub.identity_service.service;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.dto.response.common.PageResponse;
import com.coursehub.identity_service.dto.response.user.PublicUserResponse;
import com.coursehub.identity_service.model.User;

import java.util.Set;

public interface PublicUserService {
    PublicUserResponse getContentCreatorById(String id);

    User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles);

    PageResponse<PublicUserResponse> filterContentCreators(int page, int size, Double rating, String keyword);
}
