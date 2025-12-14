package com.coursehub.course_service.service;

import com.coursehub.commons.feign.CoursePriceResponse;
import com.coursehub.commons.kafka.events.*;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.enums.CourseStatus;

import java.util.Set;

public interface InternalService {
    Boolean isPublishedCourseExist(String courseId);

    Boolean isUserOwnerOfCourse(String courseId, String userId);

    Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> statuses);

    Boolean isVideoBelongCourse(String courseId, String videoId);

    String getVideoPathFromVideoId(String videoId);

    void updateCourseRating(CourseRatingUpdatedEvent event);

    CoursePriceResponse getPublishedCoursePrice(String courseId);

    void addProfilePictureToCourse(AddProfilePictureToCourseEvent event);

    void addProfilePictureToVideo(AddProfilePictureToVideoEvent event);

    Boolean isUserOwnerOfVideo(String videoId, String userId);

    void addVideoToCourse(AddVideoToCourseEvent event);

    void deleteVideoFromCourse(DeleteVideoFromCourseEvent event);

}
