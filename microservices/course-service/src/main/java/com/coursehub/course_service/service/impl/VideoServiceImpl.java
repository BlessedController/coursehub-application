package com.coursehub.course_service.service.impl;

import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.kafka.events.DeleteVideoFromCourseEvent;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.Video;
import com.coursehub.course_service.repository.VideoRepository;
import com.coursehub.course_service.service.PublicCourseService;
import com.coursehub.course_service.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

import static com.coursehub.course_service.model.enums.CourseStatus.PENDING;
import static com.coursehub.course_service.model.enums.CourseStatus.PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class VideoServiceImpl implements VideoService {

    VideoRepository videoRepository;
    PublicCourseService publicCourseService;

    //todo: add transactional
    //todo: check realtional behaviours with course entity
    @Override
    public void addVideoToCourse(AddVideoToCourseEvent event) {
        Course course = publicCourseService.findCourseByIdAndStatusIn(event.courseId(), Set.of(PUBLISHED, PENDING));

        Video video = Video.builder()
                .displayName(event.displayName())
                .videoPath(event.videoPath())
                .course(course)
                .build();

        videoRepository.save(video);

    }

    //todo: add transactional
    //todo: check realtional behaviours with course entity
    @Override
    public void deleteVideoFromCourse(DeleteVideoFromCourseEvent event) {

        Video video = videoRepository.findVideoByVideoPath(event.videoPath())
                .orElseThrow(() -> new NotFoundException("Video not found with path: " + event.videoPath()));

        videoRepository.delete(video);
    }
}
