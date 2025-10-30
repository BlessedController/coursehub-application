package com.coursehub.media_stock_service.controller;

import com.coursehub.media_stock_service.service.abstracts.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.coursehub.commons.security.UserPrincipal;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/v1/media/photo")
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class PhotoController {

    PhotoService photoService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadProfilePhoto(@RequestParam("file") MultipartFile file,
                                                   @AuthenticationPrincipal UserPrincipal principal) {

        photoService.uploadProfilePhoto(file, principal);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete-profile-photo")
    public ResponseEntity<Void> deletePhoto(@AuthenticationPrincipal UserPrincipal principal) {
        photoService.deleteProfilePhoto(principal);
        return ResponseEntity.noContent().build();
    }

}
