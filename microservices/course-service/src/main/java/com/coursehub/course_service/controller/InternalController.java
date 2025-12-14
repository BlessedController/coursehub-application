package com.coursehub.course_service.controller;

import com.coursehub.commons.feign.CoursePriceResponse;
import com.coursehub.course_service.service.InternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("${course-service.internal-base-url}")
@RequiredArgsConstructor
public class InternalController {

    private final InternalService interalService;


    //MEDIA STOCK
    @GetMapping("/course-owner-check/{courseId}/{userId}")
    ResponseEntity<Boolean> isUserOwnerOfCourse(
            @PathVariable String courseId,
            @PathVariable String userId

    ) {
        Boolean isOwner = interalService.isUserOwnerOfCourse(courseId, userId);
        return status(OK).body(isOwner);
    }

    @GetMapping("/video-owner-check/{videoId}/{userId}")
    ResponseEntity<Boolean> isUserOwnerOfVideo(@PathVariable String videoId, @PathVariable String userId) {
        Boolean body = interalService.isUserOwnerOfVideo(videoId, userId);
        return status(OK).body(body);
    }


    @GetMapping("/is-video-belong")
    ResponseEntity<Boolean> isVideoBelongCourse(@RequestParam String courseId,
                                                @RequestParam String videoId) {

        Boolean body = interalService.isVideoBelongCourse(courseId, videoId);

        return status(OK).body(body);
    }

    @GetMapping("/video-path/{videoId}")
    ResponseEntity<String> getVideoPathFromVideoId(@PathVariable String videoId) {
        String body = interalService.getVideoPathFromVideoId(videoId);
        return status(OK).body(body);
    }

    @GetMapping("/is-exist/{courseId}")
    ResponseEntity<Boolean> isPublishedCourseExist(@PathVariable String courseId) {
        Boolean body = interalService.isPublishedCourseExist(courseId);

        return status(OK).body(body);
    }

    @GetMapping("/get-price/{courseId}")
    ResponseEntity<CoursePriceResponse> getPublishedCoursePrice(@PathVariable String courseId) {
        CoursePriceResponse body = interalService.getPublishedCoursePrice(courseId);

        return status(OK).body(body);
    }


}
