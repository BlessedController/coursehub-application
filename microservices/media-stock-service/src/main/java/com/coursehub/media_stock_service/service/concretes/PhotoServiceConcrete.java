package com.coursehub.media_stock_service.service.concretes;

import com.coursehub.commons.exceptions.FileOperationException;
import com.coursehub.commons.kafka.events.AddUserProfilePhotoEvent;
import com.coursehub.commons.kafka.events.DeleteUserProfilePhotoEvent;
import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.media_stock_service.service.abstracts.PhotoService;
import io.minio.*;
import io.minio.errors.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static com.coursehub.commons.kafka.topics.PhotoTopics.ADD_PROFILE_PHOTO_TOPIC;
import static com.coursehub.commons.kafka.topics.PhotoTopics.DELETE_PROFILE_PHOTO_TOPIC;


@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceConcrete implements PhotoService {

    private final MinioClient minioClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${minio.profile-photo-bucket}")
    private String bucketName;

    @Override
    public void uploadProfilePhoto(MultipartFile file, UserPrincipal principal) {
        String fileName = principal.getUsername();

        log.info("🟦 Starting profile photo upload for userId={} (username={})", principal.getId(), principal.getUsername());

        try (InputStream inputStream = file.getInputStream()) {

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(args);
            log.info("✅ Profile photo [{}] successfully uploaded to bucket [{}] for userId={}",
                    fileName, bucketName, principal.getId());

            var event = AddUserProfilePhotoEvent.builder()
                    .profilePhotoName(fileName)
                    .userId(principal.getId())
                    .build();

            kafkaTemplate.send(ADD_PROFILE_PHOTO_TOPIC, event);
            log.info("📨 Kafka event sent to topic [{}] for userId={}", ADD_PROFILE_PHOTO_TOPIC, principal.getId());

        } catch (IOException | ErrorResponseException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                 ServerException | XmlParserException e) {

            log.error("❌ Failed to upload profile photo for userId={} (username={}): {}",
                    principal.getId(), principal.getUsername(), e.getMessage(), e);

            throw new FileOperationException("Error occurred while uploading profile photo");
        }
    }

    @Override
    public void deleteProfilePhoto(UserPrincipal principal) {
        log.info("🟥 Starting profile photo deletion for userId={} (username={})", principal.getId(), principal.getUsername());

        RemoveObjectArgs args = RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(principal.getUsername())
                .build();

        try {
            minioClient.removeObject(args);
            log.info("🗑️ Profile photo [{}] successfully deleted from bucket [{}] for userId={}",
                    principal.getUsername(), bucketName, principal.getId());

            var event = DeleteUserProfilePhotoEvent.builder()
                    .userId(principal.getId())
                    .build();

            kafkaTemplate.send(DELETE_PROFILE_PHOTO_TOPIC, event);
            log.info("📨 Kafka delete event sent to topic [{}] for userId={}", DELETE_PROFILE_PHOTO_TOPIC, principal.getId());

        } catch (ErrorResponseException | InvalidResponseException | InvalidKeyException | InsufficientDataException |
                 IOException | InternalException | NoSuchAlgorithmException | ServerException | XmlParserException e) {

            log.error("❌ Failed to delete profile photo for userId={} (username={}): {}",
                    principal.getId(), principal.getUsername(), e.getMessage(), e);

            throw new FileOperationException("Error occurred while deleting profile photo");

        }
    }
}
