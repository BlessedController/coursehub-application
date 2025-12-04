package com.coursehub.course_service.repository;

import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface VideoRepository extends JpaRepository<Video, String>, JpaSpecificationExecutor<Video> {
    Optional<Video> findVideoByVideoPath(String videoPath);

    boolean existsByIdAndCourse(String id, Course course);
}
