package com.coursehub.media_stock_service.service;

import com.coursehub.media_stock_service.dto.AddUserProfilePhotoEvent;
import com.coursehub.media_stock_service.dto.DeleteUserProfilePhotoEvent;
import com.coursehub.media_stock_service.exception.InvalidFileFormatException;
import com.coursehub.media_stock_service.security.UserPrincipal;
import com.coursehub.media_stock_service.util.MediaValidator;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static lombok.AccessLevel.PRIVATE;

@Service
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class PhotoService {
    PhotoStorageService photoStorageService;
    MediaValidator mediaValidator;
    KafkaTemplate<String, Object> kafkaTemplate;
    private static final String ADD_USER_PROFILE_PHOTO_TOPIC = "add-user-profile-photo-topic";
    private static final String DELETE_USER_PROFILE_PHOTO_TOPIC = "delete-user-profile-photo-topic";


    public void uploadProfilePhoto(MultipartFile file, UserPrincipal principal) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("File is empty. Please select a valid file to upload");
        }

        String extension = mediaValidator.getValidPhotoExtension(file);

        String username = principal.getUsername();

        String profilePhotoName = photoStorageService.storePhoto(file, username, extension);

        var addUserProfilePhotoEvent = new AddUserProfilePhotoEvent(profilePhotoName, principal.getId());

        kafkaTemplate.send(ADD_USER_PROFILE_PHOTO_TOPIC, addUserProfilePhotoEvent);
    }

    public void deletePhoto(UserPrincipal principal) {
        String currentUsername = principal.getUsername();
        mediaValidator.validateProperty(currentUsername);
        photoStorageService.deletePhoto(currentUsername);

        var deleteUserProfilePhotoEvent = new DeleteUserProfilePhotoEvent(principal.getId());

        kafkaTemplate.send(DELETE_USER_PROFILE_PHOTO_TOPIC, deleteUserProfilePhotoEvent);
    }


}
