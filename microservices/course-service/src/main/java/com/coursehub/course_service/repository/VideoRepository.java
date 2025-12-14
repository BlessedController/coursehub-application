package com.coursehub.course_service.repository;

import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.Video;
import com.coursehub.course_service.model.enums.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.*;

public interface VideoRepository extends JpaRepository<Video, String>, JpaSpecificationExecutor<Video> {
    Optional<Video> findVideoByVideoPath(String videoPath);

    boolean existsByIdAndCourse(String id, Course course);

    Optional<Video> findByIdAndStatusIn(String id, Collection<VideoStatus> statuses);
}
