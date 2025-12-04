package com.coursehub.identity_service.controller;

import com.coursehub.commons.feign.UserResponse;
import com.coursehub.identity_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${identity-service.internal-base-url}")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping("/exists/{contentCreatorId}")
    ResponseEntity<Boolean> isContentCreatorExists(@PathVariable String contentCreatorId) {
        Boolean isExist = internalService.isContentCreatorExist(contentCreatorId);
        return status(OK).body(isExist);
    }

    @GetMapping("/{userId}")
    ResponseEntity<UserResponse> getUserById(@PathVariable String userId) {
        UserResponse response = internalService.getUserById(userId);
        return status(OK).body(response);
    }

    @PostMapping("/batch")
    ResponseEntity<List<UserResponse>> getUsersBatch(@RequestBody List<String> ids) {
        List<UserResponse> response = internalService.getUsersBatch(ids);
        return status(OK).body(response);
    }
}
