package com.coursehub.identity_service.controller;

import com.coursehub.identity_service.dto.response.common.PageResponse;
import com.coursehub.identity_service.dto.response.user.PublicUserResponse;
import com.coursehub.identity_service.service.PublicUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("${identity-service.public-base-url}")
@RequiredArgsConstructor
public class PublicUserController {

    private final PublicUserService publicUserService;

    @GetMapping("/{id}")
    public ResponseEntity<PublicUserResponse> getContentCreatorById(@PathVariable(name = "id") String id) {
        PublicUserResponse body = publicUserService.getContentCreatorById(id);
        return ok(body);
    }

    @GetMapping("/filter")
    public ResponseEntity<PageResponse<PublicUserResponse>> filterContentCreators(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0.0") Double rating

    ) {

        PageResponse<PublicUserResponse> body = publicUserService.filterContentCreators(page, size, rating, keyword);
        return ok(body);
    }

}

