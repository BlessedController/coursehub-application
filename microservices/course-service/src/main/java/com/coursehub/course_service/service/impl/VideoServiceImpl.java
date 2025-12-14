package com.coursehub.course_service.service.impl;

import com.coursehub.commons.exceptions.AccessDeniedException;
import com.coursehub.commons.exceptions.NotFoundException;
import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.kafka.events.DeleteVideoFromCourseEvent;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.course_service.dto.request.EditDisplayNameRequest;
import com.coursehub.course_service.dto.response.VideoResponse;
import com.coursehub.course_service.mapper.VideoMapper;
import com.coursehub.course_service.model.Course;
import com.coursehub.course_service.model.Video;
import com.coursehub.course_service.repository.VideoRepository;
import com.coursehub.course_service.service.PublicCourseService;
import com.coursehub.course_service.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;
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

    private Video findById(String id) {
        return videoRepository.findById(id).orElseThrow(() -> new NotFoundException("Video not found with id: " + id));
    }

    @Override
    public VideoResponse getVideoById(String id) {
        Video video = this.findById(id);
        return VideoMapper.toVideoResponse(video);
    }

    @Override
    public VideoResponse editDisplayName(UserPrincipal principal, String videoId, EditDisplayNameRequest request) {

        Video video = this.findById(videoId);

        boolean isOwner = Objects.equals(video.getCourse().getInstructorId(), principal.getId());

        boolean isAdmin = principal.getAuthorities() != null &&
                principal.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("Only course owner or admin can edit video display name");
        }

        video.setDisplayName(request.displayName());
        videoRepository.save(video);

        return VideoMapper.toVideoResponse(video);
    }


}
