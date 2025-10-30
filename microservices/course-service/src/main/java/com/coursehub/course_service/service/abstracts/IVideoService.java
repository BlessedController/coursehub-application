package com.coursehub.course_service.service.abstracts;

import com.coursehub.commons.events.media.AddVideoToCourseEvent;
import com.coursehub.commons.events.media.DeleteVideoFromCourseEvent;

public interface IVideoService {
    void addVideoToCourse(AddVideoToCourseEvent event);

    void deleteVideoFromCourse(DeleteVideoFromCourseEvent event);
}
