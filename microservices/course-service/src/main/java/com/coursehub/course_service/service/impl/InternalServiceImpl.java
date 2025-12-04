package com.coursehub.course_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.feign.CoursePriceResponse;
import com.coursehub.commons.kafka.events.CourseRatingUpdatedEvent;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.Video;
import com.coursehub.course_service.model.enums.CourseStatus;
import com.coursehub.course_service.repository.CourseRepository;
import com.coursehub.course_service.repository.VideoRepository;
import com.coursehub.course_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.Set;

import static com.coursehub.course_service.model.enums.CourseStatus.PENDING;
import static com.coursehub.course_service.model.enums.CourseStatus.PUBLISHED;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

//todo: implement
@Service
@RequiredArgsConstructor
public class InternalServiceImpl implements InternalService {

    private final CourseRepository courseRepository;
    private final VideoRepository videoRepository;

    @Override
    public Boolean isPublishedCourseExist(String courseId) {
        if (!StringUtils.hasText(courseId)) return FALSE;

        return courseRepository.existsByIdAndStatusIn(courseId, Set.of(PUBLISHED));
    }

    @Override
    public Boolean isUserOwnerOfCourse(String courseId, String userId) {
        Course course = this.findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED, PENDING));

        if (Objects.equals(course.getInstructorId(), userId)) return TRUE;
        return FALSE;
    }


    @Override
    public Boolean isVideoBelongCourse(String courseId, String videoId) {
        Course course = this.findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED));
        return videoRepository.existsByIdAndCourse(videoId, course);
    }

    @Override
    public String getVideoPathFromVideoId(String videoId) {

        Video videoById = videoRepository.findById(videoId).orElseThrow(
                () -> new NotFoundException("Video not found")
        );


        return videoById.getVideoPath();
    }

    @Override
    public CoursePriceResponse getPublishedCoursePrice(String courseId) {
        Course course = this.findCourseByIdAndStatusIn(courseId, Set.of(PUBLISHED));

        return CoursePriceResponse.builder()
                .amount(course.getPrice())
                .currency(course.getCurrency())
                .build();
    }

    @Override
    public void updateCourseRating(CourseRatingUpdatedEvent event) {
        Course course = this.findCourseByIdAndStatusIn(event.courseId(), Set.of(PUBLISHED));

        course.setRating(event.averageRating());

        course.setRatingCount(event.ratingCount());

        courseRepository.save(course);
    }


    @Override
    public Course findCourseByIdAndStatusIn(String id, Set<CourseStatus> statuses) {
        return courseRepository.findByIdAndStatusIn(id, statuses)
                .orElseThrow(() -> new NotFoundException("Course not found"));
    }

}
