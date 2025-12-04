package com.coursehub.identity_service.service;

import com.coursehub.commons.feign.UserResponse;
import com.coursehub.commons.kafka.events.*;
import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.User;

import java.util.List;
import java.util.Set;

public interface InternalService {
    Boolean isContentCreatorExist(String instructorId);

    void updateContentCreatorRating(ContentCreatorRatingUpdatedEvent event);

    void addUserProfilePhotoEvent(AddUserProfilePhotoEvent event);

    void deleteUserProfilePhotoEvent(DeleteUserProfilePhotoEvent event);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    User findByIdAndUserStatusInAndUserRoleIn(String userId, Set<UserStatus> statusSet, Set<UserRole> userRoleSet);

    UserResponse getUserById(String userId);

    List<UserResponse> getUsersBatch(List<String> ids);
}
