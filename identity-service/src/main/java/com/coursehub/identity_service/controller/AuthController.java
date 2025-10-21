package com.coursehub.identity_service.controller;

import com.coursehub.identity_service.dto.request.CreateUserRequest;
import com.coursehub.identity_service.dto.request.LoginRequest;
import com.coursehub.identity_service.dto.response.GlobalSuccessMessage;
import com.coursehub.identity_service.service.absttracts.IUserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AuthController {

    IUserService userService;

    @PostMapping("/register")
    public ResponseEntity<GlobalSuccessMessage> register(@Valid @RequestBody CreateUserRequest request) {
        userService.register(request);
        return status(CREATED).body(new GlobalSuccessMessage("User created successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request,
                                        HttpServletResponse response) {

        var accessToken = userService.authenticate(request, response);

        return ok(accessToken);
    }
}
