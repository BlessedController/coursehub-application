package com.coursehub.identity_service.service;

import com.coursehub.commons.kafka.events.*;
import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.model.User;

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
}
