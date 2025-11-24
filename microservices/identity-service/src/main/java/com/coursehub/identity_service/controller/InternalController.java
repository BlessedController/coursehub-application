package com.coursehub.identity_service.controller;

import com.coursehub.identity_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${identity-service.internal-base-url}")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService internalService;

    @GetMapping("/is-exist/{instructorId}")
    ResponseEntity<Boolean> isInstructorExist(@PathVariable String instructorId) {
        Boolean isExist = internalService.isContentCreatorExist(instructorId);
        return status(OK).body(isExist);
    }




}
