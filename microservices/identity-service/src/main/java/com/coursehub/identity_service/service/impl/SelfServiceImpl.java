package com.coursehub.identity_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.identity_service.dto.request.UpdateUserInfoRequest;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.mapper.UserMapper;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.SelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.coursehub.identity_service.mapper.UserMapper.toUserSelfResponse;

@Service
@RequiredArgsConstructor
public class SelfServiceImpl implements SelfService {
    private final UserRepository userRepository;

    @Override
    public void updateSelfInfo(UserPrincipal principal, UpdateUserInfoRequest request) {
        User currentUser = this.findUserById(principal.getId());

        User updatedUser = UserMapper.updataPublicUserInfo(currentUser, request);

        userRepository.save(updatedUser);
    }

    @Override
    public UserSelfResponse getSelf(UserPrincipal principal) {
        User currentUser = this.findUserById(principal.getId());
        return toUserSelfResponse(currentUser);
    }

    private User findUserById(String id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

}
