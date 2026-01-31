package com.coursehub.media_stock_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.client.EnrollmentServiceClient;
import com.coursehub.media_stock_service.enums.AllowedVideoMimeTypes;
import com.coursehub.media_stock_service.publisher.KafkaPublisher;
import com.coursehub.media_stock_service.service.VideoService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
    private final ExecutorService videoExecutor = Executors.newFixedThreadPool(2);

    @Value("${minio.course-videos-bucket}")
    private String courseVideosBucketName;

    @Override
    public void uploadVideoFile(MultipartFile file, String courseId, String displayName, UserPrincipal principal) {

        this.validateCourseOwner(courseId, principal.getId());

        this.validateVideoFile(file);

        try {
            Path tempRawFile = Files.createTempFile("raw_upload_", ".tmp");

            file.transferTo(tempRawFile);

            log.info("File writed to disc temporary: {}", tempRawFile);

            videoExecutor.submit(() -> {
                Path hlsTempDir = null;
                try {
                    hlsTempDir = Files.createTempDirectory(HLS_STREAM_TEMP_DIR_NAME);

                    ProcessBuilder pb = this.getProcessBuilder(hlsTempDir, tempRawFile);

                    Process process = pb.start();

                    this.writeLogs(process.getInputStream());

                    int exitCode = process.waitFor();
                    if (exitCode == 0) {

                        double videoDuration = this.getVideoDuration(tempRawFile);

                        String videoPath = this.uploadToMinio(principal, courseId, hlsTempDir);

                        AddVideoToCourseEvent event = AddVideoToCourseEvent.builder()
                                .videoPath(videoPath)
                                .videoDuration(videoDuration)
                                .courseId(courseId)
                                .displayName(displayName)
                                .build();

                        kafkaPublisher.publishEvent(event);
                    }
                } catch (Exception e) {
                    log.error("ffmpeg process is failed: {}", e.getMessage());
                } finally {
                    this.deleteTempFolder(hlsTempDir);
                    try {
                        Files.deleteIfExists(tempRawFile);
                    } catch (Exception ignored) {
                    }
                }
            });

        } catch (IOException e) {
            throw new FileOperationException("File could not save disc", e);
        }
    }

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

    private String getMimeType(MultipartFile file) {
        String safeFileName = this.getSafeFileName(file.getOriginalFilename());

        String ext = safeFileName.substring(safeFileName.lastIndexOf('.') + 1);

        return AllowedVideoMimeTypes.fromExtension(ext)
                .map(AllowedVideoMimeTypes::getMimeType)
                .orElseThrow(() -> new InvalidFileFormatException("Unsupported file type: " + ext));
    }

    private String uploadToMinio(UserPrincipal principal, String courseId, Path tempDir) {
        String videoPath = UUID.randomUUID().toString();

        String basePath = principal.getId() + FILE_SEPARATOR + courseId + FILE_SEPARATOR + videoPath + FILE_SEPARATOR;

        try (Stream<Path> pathStream = Files.walk(tempDir)) {

            pathStream.filter(Files::isRegularFile).forEach(path -> {

                        String objectName = basePath +
                                tempDir.relativize(path).toString().replace("\\", FILE_SEPARATOR);

                        try (InputStream is = Files.newInputStream(path)) {

                            String fileName = path.getFileName().toString();

                            String contentType = "application/octet-stream";


                            if (fileName.endsWith(".m3u8")) {
                                contentType = "application/x-mpegURL";
                            } else if (fileName.endsWith(".ts")) {
                                contentType = "video/MP2T";
                            }


                            PutObjectArgs build = PutObjectArgs.builder()
                                    .bucket(courseVideosBucketName)
                                    .object(objectName)
                                    .contentType(contentType)
                                    .stream(is, Files.size(path), -1)
                                    .build();

                            minioClient.putObject(build);

                            log.info("Uploaded HLS chunk to MinIO: {}", objectName);

                        } catch (Exception e) {
                            log.error("MinIO upload error! Path: {}, ObjectName: {}, Error: {}", path, objectName, e.getMessage());
                            throw new FileOperationException("An error ocurred during upload to minio: " + objectName, e);
                        }

                    }
            );
        } catch (IOException e) {
            throw new FileOperationException("Failed to upload HLS file to MinIO", e);
        }
        return videoPath;
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


    private ProcessBuilder getProcessBuilder(Path tempDir, Path rawVideoPath) {

        List<String> cmd = new ArrayList<>();

        cmd.add("ffmpeg");

        cmd.add("-i");
        cmd.add(rawVideoPath.toString());

        cmd.add("-g");
        cmd.add("48");
        cmd.add("-keyint_min");
        cmd.add("48");
        cmd.add("-force_key_frames");
        cmd.add("expr:gte(t,n_forced*2)");

        this.addQuality(cmd, 0, 1920, "3000k", "128k");
        this.addQuality(cmd, 1, 1280, "1800k", "128k");
        this.addQuality(cmd, 2, 854, "900k", "96k");
        this.addQuality(cmd, 3, 640, "600k", "96k");

        for (int i = 0; i < 4; i++) {
            cmd.add("-map");
            cmd.add("0:v:0");
            cmd.add("-map");
            cmd.add("0:a:0");
        }

        cmd.add("-f");
        cmd.add("hls");
        cmd.add("-hls_time");
        cmd.add("2");
        cmd.add("-hls_playlist_type");
        cmd.add("vod");

        cmd.add("-hls_segment_filename");
        cmd.add(tempDir.toString() + "/v%v/segment%d.ts");

        cmd.add("-master_pl_name");
        cmd.add("master.m3u8");

        cmd.add("-var_stream_map");
        cmd.add("v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3");

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

    private String getSafeFileName(String originalFileName) {
        if (!StringUtils.hasText(originalFileName)) {
            throw new IllegalArgumentException("Dosya adı boş olamaz.");
        }

        String cleanedPath = StringUtils.cleanPath(originalFileName);
        if (cleanedPath.contains("..")) {
            throw new IllegalArgumentException("Geçersiz dosya yolu tespit edildi!");
        }

        int lastDotIndex = cleanedPath.lastIndexOf(".");
        if (lastDotIndex == -1 || lastDotIndex == cleanedPath.length() - 1) {
            throw new IllegalArgumentException("Geçersiz dosya uzantısı.");
        }

        String nameWithoutExt = cleanedPath.substring(0, lastDotIndex);
        String ext = cleanedPath.substring(lastDotIndex).toLowerCase();

        if (!List.of(".mp4", ".mov", ".avi", ".mkv").contains(ext)) {
            throw new IllegalArgumentException("Desteklenmeyen dosya formatı.");
        }

        String safeName = nameWithoutExt.replaceAll("[^a-zA-Z0-9-_]", "_");

        return safeName + ext;
    }

    private double getVideoDuration(Path videoPath) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ffprobe", "-v", "error", "-show_entries", "format=duration",
                    "-of", "default=noprint_wrappers=1:nokey=1", videoPath.toString()
            );

            Process p = pb.start();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line = reader.readLine();
                return line != null ? Double.parseDouble(line) : 0;
            }
        } catch (Exception e) {
            log.error("Video süresi alınamadı: {}", e.getMessage());
            return 0;
        }
    }
}
