package com.coursehub.identity_service.mapper;

import com.coursehub.identity_service.dto.request.UpdateUserInfoRequest;
import com.coursehub.identity_service.dto.response.admin.AdminUserResponse;
import com.coursehub.identity_service.dto.response.user.PublicUserResponse;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.model.User;
import org.springframework.util.StringUtils;

public class UserMapper {
    public static AdminUserResponse toAdminUserResponse(User user) {

        return AdminUserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .activationCode(user.getActivationCode())
                .gender(user.getGender())
                .aboutMe(user.getAboutMe())
                .rating(user.getRating())
                .userRole(user.getUserRole())
                .userStatus(user.getUserStatus())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

    }


    public static UserSelfResponse toUserSelfResponse(User user) {
        return new UserSelfResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserRole(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePictureName(),
                user.getGender(),
                user.getUserStatus(),
                user.getIsVerified(),
                user.getCreatedAt());
    }

    public static PublicUserResponse toPublicUserResponse(User user) {

        return new PublicUserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfilePictureName(),
                user.getGender(),
                user.getRating(),
                user.getRatingCount()
        );
    }

    public static User updataPublicUserInfo(User currentUser, UpdateUserInfoRequest request) {

        if (StringUtils.hasText(request.firstName())) {
            currentUser.setFirstName(request.firstName());
        }
        if (StringUtils.hasText(request.lastName())) {
            currentUser.setLastName(request.lastName());
        }
        if (request.gender() != null) {
            currentUser.setGender(request.gender());
        }

        return currentUser;
    }

}
