package com.coursehub.course_service.service;


import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.kafka.events.DeleteVideoFromCourseEvent;

public interface VideoService {
    void addVideoToCourse(AddVideoToCourseEvent event);

    void deleteVideoFromCourse(DeleteVideoFromCourseEvent event);
}
