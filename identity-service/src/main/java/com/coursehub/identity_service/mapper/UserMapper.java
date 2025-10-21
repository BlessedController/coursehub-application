package com.coursehub.identity_service.mapper;

import com.coursehub.identity_service.dto.request.UpdateUserInfoRequest;
import com.coursehub.identity_service.dto.response.user.UserResponseForOthers;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.model.User;

public class UserMapper {

    public static UserSelfResponse buildUserSelfResponse(User user) {
        return new UserSelfResponse(
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getUserRole(),
                user.getFirstName(),
                user.getMiddleName(),
                user.getLastName(),
                user.getGender(),
                user.getUserStatus(),
                user.getIsVerified(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }


    public static UserResponseForOthers buildUserResponseForOthers(User user) {

        return new UserResponseForOthers(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFirstName() + " " + user.getMiddleName() + " " + user.getLastName(),
                user.getGender()
        );
    }


    public static void updateUser(User currentUser, UpdateUserInfoRequest request) {
        currentUser.setFirstName(request.firstName() == null ? currentUser.getFirstName() : request.firstName());
        currentUser.setMiddleName(request.middleName() == null ? currentUser.getMiddleName() : request.middleName());
        currentUser.setLastName(request.lastName() == null ? currentUser.getLastName() : request.lastName());
        currentUser.setPhoneNumber(request.phoneNumber() == null ? currentUser.getPhoneNumber() : request.phoneNumber());
        currentUser.setGender(request.gender() == null ? currentUser.getGender() : request.gender());
    }

}
