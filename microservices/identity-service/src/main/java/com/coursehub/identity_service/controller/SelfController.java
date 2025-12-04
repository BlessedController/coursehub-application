package com.coursehub.identity_service.controller;

import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.identity_service.dto.request.UpdateUserInfoRequest;
import com.coursehub.identity_service.dto.response.user.UserSelfResponse;
import com.coursehub.identity_service.service.SelfService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${identity-service.self-base-url}")
@RequiredArgsConstructor
public class SelfController {

    private final SelfService selfService;

    @GetMapping
    public ResponseEntity<UserSelfResponse> getSelf(@AuthenticationPrincipal UserPrincipal principal) {
        return ok(selfService.getSelf(principal));
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateSelfInfo(@AuthenticationPrincipal UserPrincipal principal,
                                               @Valid @RequestBody UpdateUserInfoRequest request) {
        selfService.updateSelfInfo(principal, request);
        return status(NO_CONTENT).build();
    }

}
