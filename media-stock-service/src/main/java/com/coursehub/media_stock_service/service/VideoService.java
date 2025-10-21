package com.coursehub.media_stock_service.service;


import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.dto.AddVideoToCourseEvent;
import com.coursehub.media_stock_service.dto.DeleteVideoFromCourseEvent;
import com.coursehub.media_stock_service.exception.AccessDeniedException;
import com.coursehub.media_stock_service.exception.InvalidFileFormatException;
import com.coursehub.media_stock_service.security.UserPrincipal;
import com.coursehub.media_stock_service.util.MediaValidator;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Paths;

import static java.lang.Boolean.TRUE;
import static lombok.AccessLevel.PRIVATE;

@Service
@Slf4j
@FieldDefaults(level = PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class VideoService {

    VideoStorageService videoStorageService;
    MediaValidator mediaValidator;
    CourseServiceClient courseServiceClient;
    KafkaTemplate<String, Object> kafkaTemplate;

    static String ADD_VIDEO_TO_COURSE_TOPIC = "add-video-to-course-topic";
    static String DELETE_VIDEO_TO_COURSE_TOPIC = "delete-video-from-course-topic";


    public void uploadVideoFile(MultipartFile file,
                                UserPrincipal userPrincipal,
                                String courseId,
                                String displayName) {

        mediaValidator.validateProperty(courseId);
        mediaValidator.validateProperty(displayName);


        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("File is empty. Please select a valid file to upload");
        }

        String instructorName = userPrincipal.getUsername();

        String subFolder = Paths.get("videos", instructorName, courseId).toString();
        Boolean isValid;
        try {
            isValid = courseServiceClient.isUserOwnerOfCourse(courseId).getBody();
        } catch (Throwable throwable) {
            isValid = courseServiceClient.isUserOwnerOfCourseFallBack(courseId, throwable).getBody();
        }

        if (!TRUE.equals(isValid)) {
            throw new AccessDeniedException("Only owner can add video to course");
        }

        String filename = videoStorageService.storeVideo(file, subFolder);

        var addVideoToCourseEvent = new AddVideoToCourseEvent(
                filename,
                displayName,
                courseId,
                userPrincipal.getId()
        );

        kafkaTemplate.send(ADD_VIDEO_TO_COURSE_TOPIC, addVideoToCourseEvent);
    }

    public void deleteVideo(UserPrincipal principal, String courseId, String filename) {
        String instructorName = principal.getUsername();

        mediaValidator.validateProperty(instructorName);
        mediaValidator.validateProperty(courseId);
        mediaValidator.validateProperty(filename);

        videoStorageService.deleteVideo(instructorName, courseId, filename);

        var deleteVideoFromCourseEvent = new DeleteVideoFromCourseEvent(filename, courseId, principal.getId());

        kafkaTemplate.send(DELETE_VIDEO_TO_COURSE_TOPIC, deleteVideoFromCourseEvent);
    }

}
