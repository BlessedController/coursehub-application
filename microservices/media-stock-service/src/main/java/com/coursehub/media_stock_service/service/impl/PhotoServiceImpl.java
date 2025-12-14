package com.coursehub.media_stock_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.kafka.events.*;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.enums.AllowedPhotoMimeTypes;
import com.coursehub.media_stock_service.publisher.KafkaPublisher;
import com.coursehub.media_stock_service.service.PhotoService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoServiceImpl implements PhotoService {

    private final MinioClient minioClient;
    private final KafkaPublisher kafkaPublisher;
    private final CourseServiceClient courseServiceClient;


    @Value("${minio.user-profile-photo-bucket}")
    private String userProfilePicturesBucketName;


    @Value("${minio.course-profile-photo-bucket}")
    private String courseProfilePicturesBucketName;

    @Value("${minio.video-profile-photo-bucket}")
    private String videoProfilePicturesBucketName;

    @Override
    public void uploadUserProfilePicture(MultipartFile file, UserPrincipal principal) {
        try {

            String mimeType = this.getValidPhotoMimeType(file);

            String extension = this.getExtensionFromMimeType(mimeType);

            String objectName = principal.getUsername() + "." + extension;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(this.userProfilePicturesBucketName)
                            .object(objectName)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType(mimeType)
                            .build()
            );

            AddProfilePictureToUserEvent event = AddProfilePictureToUserEvent.builder()
                    .userId(principal.getId())
                    .profilePictureName(objectName)
                    .build();

            kafkaPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("error", e);
        }
    }

    @Override
    public void uploadCourseProfilePicture(MultipartFile file, String courseId, UserPrincipal principal) {

        try {
            this.validateCourseBelongUser(courseId, principal.getId());

            String mimeType = this.getValidPhotoMimeType(file);

            String extension = this.getExtensionFromMimeType(mimeType);

            String objectName = courseId + "." + extension;

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(this.courseProfilePicturesBucketName)
                    .object(objectName)
                    .stream(
                            file.getInputStream(),
                            file.getSize(),
                            -1
                    )
                    .contentType(mimeType)
                    .build();

            minioClient.putObject(putObjectArgs);

            AddProfilePictureToCourseEvent event = new AddProfilePictureToCourseEvent(courseId, objectName);

            kafkaPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("error", e);
        }
    }

    @Override
    public void uploadVideoProfilePicture(MultipartFile file, String courseId, String videoId, UserPrincipal principal) {

        try {
            this.validateCourseBelongUser(courseId, principal.getId());

            this.validateVideoBelongUser(videoId, principal.getId());

            String mimeType = this.getValidPhotoMimeType(file);

            String extension = this.getExtensionFromMimeType(mimeType);

            String objectName = videoId + "." + extension;

            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(this.videoProfilePicturesBucketName)
                    .object(objectName)
                    .stream(
                            file.getInputStream(),
                            file.getSize(),
                            -1
                    )
                    .contentType(mimeType)
                    .build();

            minioClient.putObject(putObjectArgs);

            AddProfilePictureToVideoEvent event = AddProfilePictureToVideoEvent.builder()
                    .videoId(videoId)
                    .profilePictureName(objectName)
                    .build();

            kafkaPublisher.publishEvent(event);

        } catch (Exception e) {
            log.error("error", e);
        }
    }

    private String getExtensionFromMimeType(String mimeType) {
        return mimeType.substring(mimeType.lastIndexOf("/") + 1);
    }

    private String getValidPhotoMimeType(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("Please select a valid file to upload.");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new InvalidFileFormatException("File has no extension");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf('.') + 1);

        return AllowedPhotoMimeTypes.fromExtension(extension)
                .map(AllowedPhotoMimeTypes::getMimeType)
                .orElseThrow(() -> new InvalidFileFormatException("Unsupported file type: " + extension));
    }

    private void validateCourseBelongUser(String courseId, String userId) {
        try {
            Boolean isUserCourseOwner = courseServiceClient
                    .isUserOwnerOfCourse(courseId, userId)
                    .getBody();

            if (!TRUE.equals(isUserCourseOwner)) {
                throw new AccessDeniedException(
                        "Only the course owner can perform this action."
                );
            }
        } catch (feign.RetryableException e) {
            log.error("Feign error: {}", e.getMessage());
            throw new CustomFeignException(e.getMessage());
        }
    }

    private void validateVideoBelongUser(String videoId, String userId) {
        try {
            Boolean isUserVideoOwner = courseServiceClient
                    .isUserOwnerOfVideo(videoId, userId)
                    .getBody();

            if (!TRUE.equals(isUserVideoOwner)) {
                throw new AccessDeniedException(
                        "Only the course owner can perform this action."
                );
            }
        } catch (feign.RetryableException e) {
            log.error("Feign error: {}", e.getMessage());
            throw new CustomFeignException(e.getMessage());
        }
    }

}
