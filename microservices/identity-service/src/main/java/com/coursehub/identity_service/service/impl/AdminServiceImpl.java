package com.coursehub.identity_service.service.impl;

import com.coursehub.commons.exceptions.ConflictException;
import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.dto.AdminUserSpecFilterRequest;
import com.coursehub.identity_service.dto.response.admin.AdminUserResponse;
import com.coursehub.identity_service.dto.response.common.PageResponse;
import com.coursehub.identity_service.mapper.UserMapper;
import com.coursehub.identity_service.model.User;
import com.coursehub.identity_service.model.enums.Gender;
import com.coursehub.identity_service.repository.UserRepository;
import com.coursehub.identity_service.service.AdminService;
import com.coursehub.identity_service.specification.AdminUserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final UserRepository userRepository;

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User not found")
        );
    }

    @Override
    public void changeUserRole(String userId, UserRole role) {
        User userById = this.getUserById(userId);

        if (userById.getUserRole().equals(role)) {
            throw new ConflictException("New role equals old role");
        }

        userById.setUserRole(role);
        userRepository.save(userById);
    }

    @Override
    public void changeUserStatus(String userId, UserStatus status) {
        User userById = this.getUserById(userId);

        if (userById.getUserStatus().equals(status)) {
            throw new ConflictException("New status equals old status");
        }

        userById.setUserStatus(status);
        userRepository.save(userById);
    }

    @Override
    public PageResponse<AdminUserResponse> filterUsers(int page, int size, String keyword, UserRole role, UserStatus status, Gender gender, Boolean isVerified, LocalDateTime minDate, LocalDateTime maxDate, Double rating, String sortBy, String orderBy) {

        if (!"asc".equalsIgnoreCase(orderBy.trim()) && !"desc".equalsIgnoreCase(orderBy.trim())) {
            orderBy = "desc";
        }

        Sort.Direction direction = Sort.Direction.fromString(orderBy);

        Sort sort = Sort.by(direction, sortBy);

        page = page <= 0 ? 1 : page;
        size = size <= 0 ? 10 : size;

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        AdminUserSpecFilterRequest request = AdminUserSpecFilterRequest.builder()
                .keyword(keyword)
                .rating(rating)
                .role(role)
                .status(status)
                .gender(gender)
                .isVerified(isVerified)
                .minDate(minDate)
                .maxDate(maxDate)
                .build();

        Specification<User> filter = AdminUserSpecification.filter(request);

        Page<User> result = userRepository.findAll(filter, pageable);

        Page<AdminUserResponse> pageResult = result.map(UserMapper::toAdminUserResponse);

        return PageResponse.<AdminUserResponse>builder()
                .content(pageResult.getContent())
                .pageNumber(pageResult.getNumber() + 1)
                .size(pageResult.getSize())
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .build();

    }


}
