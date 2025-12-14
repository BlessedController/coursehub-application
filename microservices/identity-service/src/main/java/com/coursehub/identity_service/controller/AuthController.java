package com.coursehub.identity_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.identity_service.dto.request.*;
import com.coursehub.identity_service.dto.response.common.TokenResponse;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.ResponseEntity.*;

@RestController
@RequestMapping("${identity-service.auth-base-url}")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody CreateUserRequest request) {
        authService.register(request);
        return status(CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request,
                                               HttpServletResponse response) {

        var accessToken = authService.login(request, response);

        return ok(accessToken);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request,
                                                 HttpServletResponse response) {
        TokenResponse body = authService.refresh(request, response);
        return ok(body);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        authService.logout(response);
        return noContent().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteSelf(@AuthenticationPrincipal UserPrincipal principal) {
        authService.deleteSelf(principal);
        return noContent().build();
    }

    @GetMapping("/get-verifying-email")
    public ResponseEntity<Void> getVerifyMail(@AuthenticationPrincipal UserPrincipal principal) {
        authService.getVerifyMail(principal);
        return noContent().build();
    }

    @PatchMapping("/verify/{activation-code}")
    public ResponseEntity<Void> verify(@PathVariable(name = "activation-code") String activationCode) {
        authService.verify(activationCode);
        return noContent().build();
    }

    @PutMapping("/became-instructor")
    public ResponseEntity<UserSelfResponse> becameInstructor(@AuthenticationPrincipal UserPrincipal principal) {

        UserSelfResponse response = authService.becameInstructor(principal);

        return ok(response);
    }

    @PutMapping("/update-principals")
    public ResponseEntity<Void> updateSelfPrincipals(@AuthenticationPrincipal UserPrincipal principal,
                                                     @Valid @RequestBody UpdateSelfPrincipalsRequest request
    ) {
        authService.updateSelfPrincipals(principal, request);
        return noContent().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> updateSelfPassword(@AuthenticationPrincipal UserPrincipal principal,
                                                   @Valid @RequestBody UpdateUserPasswordRequest request) {
        authService.resetPassword(principal, request);
        return noContent().build();
    }

    @PatchMapping("/suspend")
    public ResponseEntity<Void> inactivateSelf(@AuthenticationPrincipal UserPrincipal principal) {
        authService.suspendSelf(principal);
        return noContent().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> sendResetPasswordCode(@Valid @RequestBody SendResetPasswordCodeRequest request
    ) {
        authService.sendResetPasswordCode(request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-forgotten-password")
    public ResponseEntity<Void> resetForgottenPassword(@Valid @RequestBody ForgottenPasswordResetRequest request, HttpServletResponse response) {
        authService.resetForgottenPassword(request,response);
        return noContent().build();
    }



}
