package com.coursehub.identity_service.service.concretes;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.security.UserRole;
import com.coursehub.commons.security.UserStatus;
import com.coursehub.identity_service.dto.response.common.PageResponse;
import com.coursehub.identity_service.dto.response.user.PublicUserResponse;
import com.coursehub.identity_service.mapper.UserMapper;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.abstracts.PublicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.coursehub.commons.security.UserRole.ROLE_CONTENT_CREATOR;
import static com.coursehub.commons.security.UserRole.ROLE_USER;
import static com.coursehub.commons.security.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class PublicUserServiceImpl implements PublicUserService {
    private final UserRepository userRepository;


    @Override
    public PublicUserResponse getUserById(String id) {
        User targetUser = this.findUserByIdAndUserStatusInAndUserRoleIn(
                id,
                Set.of(ACTIVE),
                Set.of(ROLE_USER, ROLE_CONTENT_CREATOR)
        );
        return UserMapper.toPublicUserResponse(targetUser);
    }


    @Override
    public User findUserByIdAndUserStatusInAndUserRoleIn(String id, Set<UserStatus> statuses, Set<UserRole> userRoles) {
        return userRepository.findByIdAndUserStatusInAndUserRoleIn(id, statuses, userRoles)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public PageResponse<PublicUserResponse> filterContentCreators(int page, int size, Double rating, String keyword) {

        Pageable pageable = PageRequest.of(page, size);

        keyword = keyword.trim().toLowerCase();

        Page<User> result = userRepository.filterContentCreators(rating, keyword, pageable);

        Page<PublicUserResponse> pageResult = result.map(UserMapper::toPublicUserResponse);

        return PageResponse.<PublicUserResponse>builder()
                .content(pageResult.getContent())
                .number(pageResult.getNumber())
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();

    }


}
