package com.coursehub.identity_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.feign.UserResponse;
import com.coursehub.commons.kafka.events.*;
import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.InternalService;
import com.coursehub.identity_service.specification.UserResponseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static com.coursehub.commons.security.model.UserRole.*;
import static com.coursehub.commons.security.model.UserStatus.ACTIVE;
import static com.coursehub.commons.security.model.UserStatus.SUSPENDED;
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
    public void addUserProfilePhotoEvent(AddProfilePictureToUserEvent event) {
        User user = this.findByIdAndUserStatusInAndUserRoleIn(event.userId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR));
        user.setProfilePictureName(event.profilePictureName());
        userRepository.save(user);
    }


    @Override
    public void deleteUserProfilePhotoEvent(DeleteUserProfilePhotoEvent event) {
        User user = this.findByIdAndUserStatusInAndUserRoleIn(event.userId(), Set.of(ACTIVE), Set.of(ROLE_USER, ROLE_CONTENT_CREATOR, ROLE_ADMIN));
        user.setProfilePictureName(null);
        userRepository.save(user);
    }

    @Override
    public User findByIdAndUserStatusInAndUserRoleIn(String userId, Set<UserStatus> statusSet, Set<UserRole> userRoleSet) {
        return userRepository.findByIdAndUserStatusInAndUserRoleIn(userId, statusSet, userRoleSet).orElseThrow(
                () -> new NotFoundException("Content creator not found")
        );
    }

    @Override
    public UserResponse getUserById(String userId) {
        User user = this.findByIdAndUserStatusInAndUserRoleIn(userId, Set.of(ACTIVE, SUSPENDED), Set.of(ROLE_CONTENT_CREATOR));

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();

    }

    @Override
    public List<UserResponse> getUsersBatch(List<String> ids) {

        if (ids == null || ids.isEmpty()) {
            return List.of();
        }

        Specification<User> filter = UserResponseSpecification.filter(Set.of(ACTIVE, SUSPENDED), ids);


        List<User> all = userRepository.findAll(filter);

        return all.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .build()).toList();
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
