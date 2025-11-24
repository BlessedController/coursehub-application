package com.coursehub.identity_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.kafka.events.*;
import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.coursehub.commons.security.UserRole.*;
import static com.coursehub.commons.security.UserStatus.ACTIVE;
import static java.lang.Boolean.FALSE;
import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class InternalServiceImpl implements InternalService {
    private final UserRepository userRepository;

    @Override
    public Boolean isContentCreatorExist(String instructorId) {
        if (!hasText(instructorId)) return FALSE;

        return userRepository.findById(instructorId)
                .map(user -> ROLE_CONTENT_CREATOR.equals(user.getUserRole()) && ACTIVE.equals(user.getUserStatus()))
                .orElse(FALSE);
    }

    @Override
    public void updateContentCreatorRating(ContentCreatorRatingUpdatedEvent event) {
        User contentCreator = this.findByIdAndUserStatusInAndUserRoleIn(event.contentCreatorId(), Set.of(ACTIVE), Set.of(ROLE_CONTENT_CREATOR));

        contentCreator.setRating(event.averageRating());
        contentCreator.setRatingCount(event.ratingCount());

        userRepository.save(contentCreator);
    }

    @Override
    public void addUserProfilePhotoEvent(AddUserProfilePhotoEvent event) {
        User user = this.findByIdAndUserStatusInAndUserRoleIn(event.userId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR, ROLE_ADMIN));
        user.setProfilePhotoName(event.profilePhotoName());
        userRepository.save(user);
    }


    @Override
    public void deleteUserProfilePhotoEvent(DeleteUserProfilePhotoEvent event) {
        User user = this.findByIdAndUserStatusInAndUserRoleIn(event.userId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR, ROLE_ADMIN));
        user.setProfilePhotoName(null);
        userRepository.save(user);
    }

    @Override
    public User findByIdAndUserStatusInAndUserRoleIn(String userId, Set<UserStatus> statusSet, Set<UserRole> userRoleSet) {
        return userRepository.findByIdAndUserStatusInAndUserRoleIn(userId, statusSet, userRoleSet).orElseThrow(
                () -> new NotFoundException("Content creator not found")
        );
    }
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

}
