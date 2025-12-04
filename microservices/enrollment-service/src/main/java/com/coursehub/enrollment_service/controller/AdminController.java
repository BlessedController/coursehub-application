package com.coursehub.enrollment_service.controller;

import com.coursehub.enrollment_service.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${enrollment-service.admin-controller-base-url}")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;


}
