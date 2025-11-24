package com.coursehub.media_stock_service.service.concretes;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.kafka.events.DeleteVideoFromCourseEvent;
import com.coursehub.commons.security.UserPrincipal;
import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.client.EnrollmentServiceClient;
import com.coursehub.media_stock_service.dto.StreamResponse;
import com.coursehub.media_stock_service.enums.AllowedVideoMimeTypes;
import com.coursehub.media_stock_service.service.abstracts.VideoService;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
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
import java.util.Arrays;
import java.util.UUID;

import static com.coursehub.commons.kafka.topics.VideoTopics.ADD_VIDEO_TO_COURSE_TOPIC;
import static com.coursehub.commons.kafka.topics.VideoTopics.DELETE_VIDEO_FROM_COURSE_TOPIC;
import static java.lang.Boolean.TRUE;


@Service
@RequiredArgsConstructor
@Slf4j
public class VideoServiceImpl implements VideoService {

    private final MinioClient minioClient;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final CourseServiceClient courseServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;


    @Value("${minio.course-videos-bucket:course-videos}")
    private String bucketName;

    private static final int PRESIGNED_URL_EXPIRY_SECONDS = 60 * 60 * 2;
    private static final String PATH_SEPARATOR = "/";


    @Override
    public void uploadVideoFile(MultipartFile file, String courseId, String displayName, UserPrincipal principal) {

        this.validateCourseOwner(courseId, principal.getId());
        this.validateVideoFile(file);

        String mimeType = file.getContentType();
        validateMimeType(mimeType);


        try (InputStream inputStream = file.getInputStream()) {


            String videoPath = principal.getUsername() + PATH_SEPARATOR + courseId + PATH_SEPARATOR + UUID.randomUUID();

            PutObjectArgs args = PutObjectArgs.builder()
                    .bucket(this.bucketName)
                    .object(videoPath)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(mimeType)
                    .build();

            minioClient.putObject(args);

            var event = AddVideoToCourseEvent.builder()
                    .videoPath(videoPath)
                    .displayName(displayName)
                    .courseId(courseId)
                    .build();

            kafkaTemplate.send(ADD_VIDEO_TO_COURSE_TOPIC, event);
            log.info("📤 Sent AddVideoToCourseEvent for courseId={} videoPath={}", courseId, videoPath);

        } catch (ServerException | XmlParserException | InvalidKeyException | NoSuchAlgorithmException |
                 IOException |
                 InsufficientDataException | ErrorResponseException | InvalidResponseException |
                 InternalException e) {

            log.error("❌ Error storing video file: {}", e.getMessage(), e);
            throw new FileOperationException("An error occurred while uploading the video. Please try again later.");
        }
    }

    @Override
    public void deleteVideoFile(UserPrincipal principal, String courseId, String videoId) {
        this.validateCourseOwner(courseId, principal.getId());

        String videoPath = courseServiceClient.getVideoPathFromVideoId(videoId).getBody();
        if (videoPath == null) {
            throw new FileOperationException("Video path not found for given video ID: " + videoId);
        }

        try {
            log.info("🗑️ [{}] requested to delete video [{}] from bucket [{}]", principal.getUsername(), videoPath, bucketName);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(videoPath)
                            .build()
            );

            log.info("✅ Video [{}] successfully deleted from MinIO", videoPath);

            var event = DeleteVideoFromCourseEvent.builder()
                    .videoPath(videoPath)
                    .courseId(courseId)
                    .build();

            kafkaTemplate.send(DELETE_VIDEO_FROM_COURSE_TOPIC, event);

        } catch (Exception e) {
            log.error("❌ Error deleting video [{}]: {}", videoPath, e.getMessage(), e);
            throw new FileOperationException("An error occurred while deleting the video. Please try again later.");
        }
    }

    @Override
    public StreamResponse streamVideo(String courseId, String videoId, UserPrincipal principal) {
        validateHasAccessToStream(courseId, principal.getId());
        validateVideoBelongsToCourse(courseId, videoId);

        String videoPath = courseServiceClient.getVideoPathFromVideoId(videoId).getBody();
        if (videoPath == null) {
            throw new StreamingException("Video path could not be found for videoId: " + videoId);
        }

        try {
            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(videoPath)
                            .expiry(PRESIGNED_URL_EXPIRY_SECONDS)
                            .build()
            );

            log.info("🎬 Generated presigned URL for video [{}] by user [{}]", videoPath, principal.getUsername());

            return StreamResponse.builder()
                    .videoUrl(presignedUrl)
                    .build();

        } catch (Exception e) {
            log.error("❌ Error generating presigned URL for video [{}]: {}", videoId, e.getMessage(), e);
            throw new StreamingException("An error occurred while preparing the video stream. Please try again later.");
        }
    }

    private void validateMimeType(String mimeType) {
        boolean valid = Arrays.stream(AllowedVideoMimeTypes.values())
                .anyMatch(type -> type.getMimeType().equalsIgnoreCase(mimeType));

        if (!valid) {
            throw new InvalidFileFormatException("Invalid mime type: " + mimeType);
        }
    }

    private void validateVideoFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("Please select a valid file to upload.");
        }
    }

    private void validateCourseOwner(String courseId, String userId) {
        Boolean isUserCourseOwner = courseServiceClient.isUserOwnerOfCourse(courseId, userId).getBody();

        if (!TRUE.equals(isUserCourseOwner)) {
            throw new AccessDeniedException("Only the course owner can perform this action.");
        }
    }


    private void validateHasAccessToStream(String courseId, String userId) {
        Boolean isOwner = courseServiceClient.isUserOwnerOfCourse(courseId, userId).getBody();
        Boolean hasAccess = enrollmentServiceClient.hasEnrolledByUser(courseId, userId).getBody();
        if (!TRUE.equals(hasAccess) && !TRUE.equals(isOwner)) {
            throw new AccessDeniedException("Access denied");
        }
    }

    private void validateVideoBelongsToCourse(String courseId, String videoId) {
        Boolean isVideoBelongToCourse = courseServiceClient.isVideoBelongCourse(courseId, videoId).getBody();
        if (!TRUE.equals(isVideoBelongToCourse)) {
            throw new AccessDeniedException("This video does not belong to the specified course.");
        }
    }
}
