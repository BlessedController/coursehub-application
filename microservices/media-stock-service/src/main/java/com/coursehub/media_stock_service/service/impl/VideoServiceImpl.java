package com.coursehub.media_stock_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.RedisUtil;
import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.client.EnrollmentServiceClient;
import com.coursehub.media_stock_service.enums.AllowedVideoMimeTypes;
import com.coursehub.media_stock_service.publisher.KafkaPublisher;
import com.coursehub.media_stock_service.service.VideoService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.http.Method;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final MinioClient minioClient;
    private final CourseServiceClient courseServiceClient;
    private final EnrollmentServiceClient enrollmentServiceClient;
    private final KafkaPublisher kafkaPublisher;
    private final RedisUtil redisUtil;

    private static final String FILE_SEPARATOR = "/";
    private static final String RANGE_HEADER = "Range";
    private static final String HLS_STREAM_TEMP_DIR_NAME = "hls_stream_";
    private static final long PRESIGNED_CACHE_TTL_SECONDS = 30;
    private static final int PRESIGNED_URL_EXPRIY_SECONDS = 600;


    @Value("${minio.course-videos-bucket}")
    private String courseVideosBucketName;

    private String getMimeType(MultipartFile file) {
        String original = file.getOriginalFilename();

        if (original == null || !original.contains(".")) {
            throw new InvalidFileFormatException("File has no extension");
        }

        String ext = original.substring(original.lastIndexOf('.') + 1);

        return AllowedVideoMimeTypes.fromExtension(ext)
                .map(AllowedVideoMimeTypes::getMimeType)
                .orElseThrow(() -> new InvalidFileFormatException("Unsupported file type: " + ext));
    }

    private void transferVideoFileToFfmpeg(Process process, MultipartFile file) {
        try (
                OutputStream ffmpegOutputStream = process.getOutputStream();
                InputStream fileInputStream = file.getInputStream()
        ) {
            fileInputStream.transferTo(ffmpegOutputStream);
        } catch (IOException exception) {
            throw new FileOperationException(
                    "An error occurred during streaming video file to FFmpeg.",
                    exception
            );
        }
    }

    private String uploadToMinio(UserPrincipal principal, String courseId, Path tempDir) {
        String videoPath = UUID.randomUUID().toString();

        String basePath = principal.getId() + FILE_SEPARATOR + courseId + FILE_SEPARATOR + videoPath + FILE_SEPARATOR;

        try (Stream<Path> pathStream = Files.walk(tempDir)) {

            pathStream.filter(Files::isRegularFile).forEach(path -> {

                        String objectName = basePath +
                                tempDir.relativize(path).toString().replace("\\", FILE_SEPARATOR);

                        try (InputStream is = Files.newInputStream(path)) {

                            minioClient.putObject(
                                    PutObjectArgs.builder()
                                            .bucket(courseVideosBucketName)
                                            .object(objectName)
                                            .stream(is, Files.size(path), -1)
                                            .build()
                            );

                            log.info("Uploaded HLS chunk to MinIO: {}", objectName);

                        } catch (Exception e) {
                            throw new FileOperationException("Failed to upload HLS file to MinIO", e);
                        }

                    }
            );
        } catch (IOException e) {
            throw new FileOperationException("Failed to upload HLS file to MinIO", e);
        }
        return videoPath;
    }

    @Override
    public void uploadVideoFile(MultipartFile file,
                                String courseId,
                                String displayName,
                                UserPrincipal principal) {


        this.validateCourseOwner(courseId, principal.getId());
        this.validateVideoFile(file);
        Path tempDir = null;

        try {
            tempDir = Files.createTempDirectory(HLS_STREAM_TEMP_DIR_NAME);
            log.info("Temp HLS dir created: {}", tempDir);

            ProcessBuilder pb = this.getProcessBuilder(tempDir);

            Process process = pb.start();

            this.writeLogs(process.getInputStream());

            this.transferVideoFileToFfmpeg(process, file);

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                throw new FileOperationException("FFmpeg failed with exit code: " + exitCode);
            }

            log.info("FFmpeg finished successfully for courseId={}", courseId);

            String videoPath = this.uploadToMinio(principal, courseId, tempDir);

            AddVideoToCourseEvent event = AddVideoToCourseEvent.builder()
                    .displayName(displayName)
                    .courseId(courseId)
                    .videoPath(videoPath)
                    .build();

            kafkaPublisher.publishAddVideoToCourseEvent(event);

        } catch (Exception e) {
            log.error("Streaming HLS processing failed: {}", e.getMessage(), e);
            throw new FileOperationException("Streaming HLS processing failed", e);
        } finally {
            // 9) Temp klasörü her durumda temizlemeyi dene
            if (tempDir != null) {
                this.deleteTempFolder(tempDir);
            }
        }
    }


    @Override
    public void streamVideo(
            UserPrincipal principal,
            String creatorId,
            String courseId,
            String videoId,
            HttpServletRequest request,
            HttpServletResponse response) {

        this.validateUserHasAccessToStream(courseId, principal.getId());

        String remaining = this.getRemainingFromPath(request);

        String objectPath =
                creatorId + "/" + courseId + "/" + videoId + "/" + remaining;

        try {
            String presignedUrl = this.getPresignedUrl(objectPath);

            HttpURLConnection conn = this.getConnection(presignedUrl, request);

            this.addResponseHeaders(conn, response);

            this.streamData(conn, response);

        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            this.sendJson(response, "{\"error\":\"file_not_found\"}");
        } catch (Exception e) {
            log.error("❌ Failed to stream video {} -> {}", objectPath, e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            this.sendJson(response, "{\"error\":\"stream_failed\"}");
        }
    }

    private void sendJson(HttpServletResponse response, String json) {
        try {
            response.setContentType("application/json");
            response.getWriter().write(json);
        } catch (IOException ignored) {
        }
    }


    @Override
    public void deleteVideoFile(UserPrincipal principal, String courseId, String videoId) {
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


    private String getPresignedUrl(String objectPath) {

        String cacheKey = "presigned:" + objectPath;

        String cachedValue = redisUtil.getDataFromCache(cacheKey);

        if (cachedValue != null) return cachedValue;

        try {

            minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(courseVideosBucketName)
                            .object(objectPath)
                            .build()
            );

            String presignedUrl = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(courseVideosBucketName)
                            .object(objectPath)
                            .method(Method.GET)
                            .expiry(PRESIGNED_URL_EXPRIY_SECONDS)
                            .build()
            );

            redisUtil.saveToCache(cacheKey, presignedUrl, PRESIGNED_CACHE_TTL_SECONDS, ChronoUnit.SECONDS);

            return presignedUrl;

        } catch (ErrorResponseException e) {
            if ("NoSuchKey".equals(e.errorResponse().code())) {
                throw new NotFoundException("Requested video file not found: " + objectPath);
            }
            throw new StreamingException("Failed to access MinIO");
        } catch (Exception e) {
            throw new StreamingException("Failed to access MinIO");
        }
    }

    private void addResponseHeaders(HttpURLConnection conn, HttpServletResponse response) throws IOException {
        response.setStatus(conn.getResponseCode());

        conn.getHeaderFields().forEach((key, values) -> {
            if (key != null && values != null) {
                values.forEach(v -> response.addHeader(key, v));
            }
        });

    }

    private HttpURLConnection getConnection(String presignedUrl, HttpServletRequest request) throws IOException {
        String range = request.getHeader(RANGE_HEADER);

        HttpURLConnection conn = (HttpURLConnection)
                URI.create(presignedUrl).toURL().openConnection();

        if (range != null) {
            conn.setRequestProperty(RANGE_HEADER, range);
        }

        conn.setRequestProperty("Connection", "close"); // Important for streaming stability
        conn.setReadTimeout(30000);
        conn.setConnectTimeout(10000);
        conn.connect();
        return conn;
    }

    private void streamData(HttpURLConnection conn, HttpServletResponse response) throws IOException {
        try (InputStream is = conn.getInputStream();
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int len;

            while ((len = is.read(buffer)) != -1) {
                os.write(buffer, 0, len);

                os.flush();
                response.flushBuffer();
            }

            os.flush();
        }
    }


    private void writeLogs(InputStream inputStream) {

        Runnable task = () -> {
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(inputStream))) {

                String line;

                while ((line = reader.readLine()) != null) {
                    log.info("[FFMPEG] {}", line);
                }

            } catch (Exception e) {
                log.error("FFmpeg log read error: {}", e.getMessage());
            }
        };

        String threadName = "ffmpeg-log-reader";

        Thread thread = new Thread(task, threadName);

        thread.start();
    }

    private void deleteTempFolder(Path folder) {
        try (Stream<Path> walk = Files.walk(folder)) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (Exception ignored) {
                        }
                    });
            log.info("Temp HLS folder deleted: {}", folder);
        } catch (Exception e) {
            log.warn("Failed to delete temp folder {} -> {}", folder, e.getMessage());
        }
    }

    private void validateMimeType(String mimeType) {
        boolean isVideoMimeTypeAllowed = AllowedVideoMimeTypes.isVideoMimeTypeAllowed(mimeType);

        if (!isVideoMimeTypeAllowed) {
            throw new InvalidFileFormatException("Invalid mime type: " + mimeType);
        }
    }

    private void validateVideoFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("Please select a valid file to upload.");
        }

        String mimeType = this.getMimeType(file);

        this.validateMimeType(mimeType);

    }

    private void validateCourseOwner(String courseId, String userId) {
        try {
            Boolean isUserCourseOwner = courseServiceClient
                    .isUserOwnerOfCourse(courseId, userId)
                    .getBody();

            if (!TRUE.equals(isUserCourseOwner)) {
                throw new AccessDeniedException(
                        "Only the course owner can perform this action."
                );
            }
        } catch (feign.RetryableException e) {
            log.error("Feign error: {}", e.getMessage());
            throw new CustomFeignException(e.getMessage());
        }
    }

    private void validateUserHasAccessToStream(String courseId, String userId) {
        try {

            Boolean isUserCourseOwner = courseServiceClient
                    .isUserOwnerOfCourse(courseId, userId)
                    .getBody();

            Boolean hasEnrolledByUser = enrollmentServiceClient
                    .hasEnrolledByUser(courseId, userId)
                    .getBody();

            if (!TRUE.equals(hasEnrolledByUser) && !TRUE.equals(isUserCourseOwner)) {
                throw new AccessDeniedException("Only the user who enroll this course can perform this action.");
            }
        } catch (feign.RetryableException e) {
            log.error("Feign error: {}", e.getMessage());
            throw new CustomFeignException(e.getMessage());
        }

    }


    private ProcessBuilder getProcessBuilder(Path tempDir) {

        List<String> cmd = new ArrayList<>();

        cmd.add("ffmpeg");
        cmd.add("-i");
        cmd.add("pipe:0");

        // 🔹 KEYFRAME AYARLARI
        cmd.add("-g");
        cmd.add("48");
        cmd.add("-keyint_min");
        cmd.add("48");
        cmd.add("-force_key_frames");
        cmd.add("expr:gte(t,n_forced*2)");

        // 🔹 KALİTE PROFİLLERİNİ TEK SATIRDA EKLE
        addQuality(cmd, 0, 1920, "3000k", "128k");  // 1080p
        addQuality(cmd, 1, 1280, "1800k", "128k"); // 720p
        addQuality(cmd, 2, 854, "900k", "96k");  // 480p
        addQuality(cmd, 3, 640, "600k", "96k");  // 360p

        // 🔹 STREAM MAPPING (TEKRAR YOK)
        for (int i = 0; i < 4; i++) {
            cmd.add("-map");
            cmd.add("0:v:0");
            cmd.add("-map");
            cmd.add("0:a:0");
        }

        // 🔹 HLS AYARLARI
        cmd.add("-f");
        cmd.add("hls");
        cmd.add("-hls_time");
        cmd.add("2");
        cmd.add("-hls_playlist_type");
        cmd.add("vod");
        cmd.add("-hls_segment_filename");
        cmd.add(tempDir + "/v%v/segment%d.ts");
        cmd.add("-master_pl_name");
        cmd.add("master.m3u8");
        cmd.add("-var_stream_map");
        cmd.add("v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3");

        // 🔹 ÇIKTI PLAYLIST DOSYASI
        cmd.add(tempDir + "/v%v/playlist.m3u8");

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.redirectErrorStream(true);
        return pb;
    }


    private void addQuality(List<String> cmd, int index, int width, String videoBitrate, String audioBitrate) {
        cmd.add("-filter:v:" + index);
        cmd.add("scale=w=" + width + ":h=-2");
        cmd.add("-c:v:" + index);
        cmd.add("libx264");
        cmd.add("-b:v:" + index);
        cmd.add(videoBitrate);
        cmd.add("-c:a:" + index);
        cmd.add("aac");
        cmd.add("-b:a:" + index);
        cmd.add(audioBitrate);
    }


}
