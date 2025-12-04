package com.coursehub.identity_service.controller;

import com.coursehub.commons.security.model.UserRole;
import com.coursehub.commons.security.model.UserStatus;
import com.coursehub.identity_service.dto.response.admin.AdminUserResponse;
import com.coursehub.identity_service.dto.response.common.PageResponse;
import com.coursehub.identity_service.model.enums.Gender;
import com.coursehub.identity_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${identity-service.admin-base-url}")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    @PatchMapping("/change-role/{userId}")
    public ResponseEntity<Void> changeUserRole(@PathVariable String userId, @RequestParam UserRole role) {
        adminService.changeUserRole(userId, role);
        return noContent().build();
    }

    @PatchMapping("/change-status/{userId}")
    public ResponseEntity<Void> changeUserStatus(@PathVariable String userId, @RequestParam UserStatus status) {
        adminService.changeUserStatus(userId, status);
        return noContent().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<AdminUserResponse>> filterUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) Gender gender,
            @RequestParam(required = false) Boolean isVerified,
            @RequestParam(required = false) LocalDateTime minDate,
            @RequestParam(required = false) LocalDateTime maxDate,
            @RequestParam(defaultValue = "0.0") Double rating,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String orderBy

    ) {

        PageResponse<AdminUserResponse> body = adminService.filterUsers(page, size, keyword, role, status, gender, isVerified, minDate, maxDate, rating, sortBy, orderBy);
        return ok(body);
    }


}
