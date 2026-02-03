package com.coursehub.media_stock_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.client.EnrollmentServiceClient;
import com.coursehub.media_stock_service.service.VideoStreamingService;
import com.coursehub.media_stock_service.util.RedisUtil;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

import java.io.*;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoStreamingServiceImpl implements VideoStreamingService {

    private final MinioClient minioClient;
    private final CourseServiceClient courseServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;
    private final RedisUtil redisUtil;

    private static final String RANGE_HEADER = "Range";

    @Value("${minio.course-videos-bucket}")
    private String courseVideosBucketName;

    @Override
    public void streamVideo(
            UserPrincipal principal,
            String creatorId,
            String courseId,
            String videoPath,
            HttpServletRequest request,
            HttpServletResponse response) {

        String remaining = this.getRemainingFromPath(request);

        String sessionKey = "stream:session:" + principal.getId() + ":" + videoPath;

        String hasSession = redisUtil.getDataFromCache(sessionKey);

        if (hasSession == null) {
            log.info("Oturum bulunamadı, yetki kontrolleri yapılıyor: {}", sessionKey);
            this.validateUserHasAccessToStream(courseId, principal.getId());

            redisUtil.saveToCache(sessionKey, "ACTIVE", 1L, ChronoUnit.HOURS);
        }

        String objectPath =
                creatorId + "/" + courseId + "/" + videoPath + "/" + remaining;

        try {

            StatObjectResponse videoMetadata;

            if (remaining.endsWith("ts")) {
                response.setContentType("video/MP2T");

                String sizeKey = remaining + "-size-" + videoPath;

                Long size = redisUtil.getDataFromCache(sizeKey);

                if (size == null) {
                    videoMetadata = this.getVideoMetadata(objectPath);
                    size = videoMetadata.size();
                    redisUtil.saveToCache(sizeKey, size, 1L, ChronoUnit.HOURS);
                }
                response.setContentLengthLong(size);
            } else {
                videoMetadata = this.getVideoMetadata(objectPath);
                this.addResponseHeaders(videoMetadata, response);
            }

            GetObjectArgs objectArgs = this.getObjectArgs(objectPath, request.getHeader(RANGE_HEADER));

            this.streamData(objectArgs, response);

        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            this.sendJson(response, "{\"error\":\"file_not_found\"}");
        } catch (Exception e) {
            log.error("❌ Failed to stream video {} -> {}", objectPath, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            this.sendJson(response, "{\"error\":\"stream_failed\"}");
        }
    }


    public GetObjectArgs getObjectArgs(String objectName, String range) {

        GetObjectArgs.Builder argsBuilder = GetObjectArgs.builder()
                .bucket(courseVideosBucketName)
                .object(objectName);

        if (StringUtils.hasText(range)) {
            argsBuilder.extraHeaders(Map.of("Range", range));
        }

        return argsBuilder.build();
    }


    private void sendJson(HttpServletResponse response, String json) {
        try {
            response.setContentType("application/json");
            response.getWriter().write(json);
        } catch (IOException ignored) {
        }
    }


    private String getRemainingFromPath(HttpServletRequest request) {
        String fullPath = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE
        );

        String bestMatch = (String) request.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE
        );

        return new AntPathMatcher()
                .extractPathWithinPattern(bestMatch, fullPath);
    }

    private void addResponseHeaders(StatObjectResponse metadata, HttpServletResponse response) {
        response.setContentType(metadata.contentType());

        response.setContentLengthLong(metadata.size());

        metadata.headers().names().forEach(name -> {
            if (!name.equalsIgnoreCase("Content-Type") && !name.equalsIgnoreCase("Content-Length")) {
                response.addHeader(name, metadata.headers().get(name));
            }
        });
    }


    private StatObjectResponse getVideoMetadata(String objectName) {
        try {
            return minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(courseVideosBucketName)
                            .object(objectName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                log.error("Dosya MinIO üzerinde bulunamadı: {}", objectName);
                throw new NotFoundException("Video dosyası mevcut değil: " + objectName);
            }
            throw new NotFoundException("MinIO hata yanıtı döndürdü: " + e.getMessage());
        } catch (Exception e) {
            log.error("Meta veri çekilirken beklenmedik hata: {}", e.getMessage());
            throw new NotFoundException("Video meta verisi alınamadı");
        }
    }

    private void streamData(GetObjectArgs args, HttpServletResponse response) {
        try (
                InputStream is = minioClient.getObject(args);
                OutputStream os = response.getOutputStream()
        ) {

            byte[] buffer = new byte[8192];

            int len;

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
            response.flushBuffer();

        } catch (Exception e) {
            throw new StreamingException("Failed to access MinIO");
        }
    }

    private void validateUserHasAccessToStream(String courseId, String userId) {
        try {

            Boolean isUserCourseOwner = courseServiceClient
                    .isUserOwnerOfCourse(courseId, userId)
                    .getBody();

            if (TRUE.equals(isUserCourseOwner)) {
                return;
            }

            Boolean hasEnrolledByUser = enrollmentServiceClient
                    .hasEnrolledByUser(courseId, userId)
                    .getBody();

            if (!TRUE.equals(hasEnrolledByUser) && !TRUE.equals(isUserCourseOwner)) {
                log.error("Access denied exception throwed for course id:{} and user id: {}", courseId, userId);
                throw new AccessDeniedException("Only the user who enroll this course can perform this action.");
            }
        } catch (feign.RetryableException e) {
            throw new CustomFeignException(e.getMessage());
        }

    }


}
