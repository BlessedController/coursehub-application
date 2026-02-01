package com.coursehub.media_stock_service.service.impl;

import com.coursehub.commons.exceptions.*;
import com.coursehub.commons.kafka.events.AddVideoToCourseEvent;
import com.coursehub.commons.security.model.UserPrincipal;
import com.coursehub.media_stock_service.client.CourseServiceClient;
import com.coursehub.media_stock_service.dto.VideoMetaData;
import com.coursehub.media_stock_service.enums.AllowedVideoMimeTypes;
import com.coursehub.media_stock_service.publisher.KafkaPublisher;
import com.coursehub.media_stock_service.service.MinioService;
import com.coursehub.media_stock_service.service.VideoProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

import static java.lang.Boolean.TRUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoProcessingServiceImpl implements VideoProcessingService {

    private static final String HLS_STREAM_TEMP_DIR_NAME = "hls_stream_";
    private static final String RAW_TEMP_FILE_PREFIX = "raw_upload_";
    private static final String RAW_TEMP_FILE_SUFFIX = ".tmp";

    private final MinioService minioService;
    private final CourseServiceClient courseServiceClient;
    private final KafkaPublisher kafkaPublisher;


    @Override
    public void uploadVideoFile(MultipartFile file, String courseId, String displayName, UserPrincipal principal) {
        this.validateCourseOwner(courseId, principal.getId());
        this.validateVideoFile(file);

        try {

            Path hlsTempDir = Files.createTempDirectory(HLS_STREAM_TEMP_DIR_NAME);

            Path tempRawFile = this.createTempVideoFile(file);

            int exitCode = this.doProcessOnVideoFile(hlsTempDir, tempRawFile);

            if (exitCode == 0) {
                String randomVideoName = this.generateRandomVideoName();
                double videoDuration = this.getVideoDuration(tempRawFile);
                VideoMetaData videoMetaData = new VideoMetaData(randomVideoName, displayName, principal.getId(), courseId, videoDuration);
                minioService.uploadToMinio(videoMetaData, hlsTempDir);
                this.createAndPublishAddVideoToCourseEvent(videoMetaData);
            }
            this.deleteTempFolder(hlsTempDir);
            Files.deleteIfExists(tempRawFile);

        } catch (IOException e) {
            log.error("IO exception occured: {}", e.getMessage());
            throw new FileOperationException("IO exception occured");
        }
    }

    private void validateCourseOwner(String courseId, String userId) {
        Boolean isUserCourseOwner = courseServiceClient
                .isUserOwnerOfCourse(courseId, userId)
                .getBody();

        if (!TRUE.equals(isUserCourseOwner)) {
            throw new AccessDeniedException(
                    "Only the course owner can perform this action."
            );
        }
    }

    private void validateVideoFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new InvalidFileFormatException("Please select a valid file to upload.");
        }

        String mimeType = this.getMimeType(file);

        this.validateMimeType(mimeType);

    }

    private String getMimeType(MultipartFile file) {
        String safeFileName = this.getSafeFileName(file.getOriginalFilename());

        String ext = safeFileName.substring(safeFileName.lastIndexOf('.') + 1);

        return AllowedVideoMimeTypes.fromExtension(ext)
                .map(AllowedVideoMimeTypes::getMimeType)
                .orElseThrow(() -> new InvalidFileFormatException("Unsupported file type: " + ext));
    }

    private void validateMimeType(String mimeType) {
        boolean isValidVideoMimeType = AllowedVideoMimeTypes.isValidVideoMimeType(mimeType);

        if (!isValidVideoMimeType) {
            throw new InvalidFileFormatException("Invalid mime type: " + mimeType);
        }
    }

    private Path createTempVideoFile(MultipartFile file) {
        Path tempRawFile;
        try {
            tempRawFile = Files.createTempFile(RAW_TEMP_FILE_PREFIX, RAW_TEMP_FILE_SUFFIX);
            file.transferTo(tempRawFile);
        } catch (IOException e) {
            log.error("IO exception occured during creating temp file or transferring raw video file to temp file : {}", e.getMessage());
            throw new FileOperationException("An IO exception occured while creating raw video file");
        }
        return tempRawFile;
    }

    private int doProcessOnVideoFile(Path hlsTempDir, Path tempRawFile) {

        ProcessBuilder pb = this.getProcessBuilder(hlsTempDir, tempRawFile);

        Process process;
        try {
            process = pb.start();
            return process.waitFor();
        } catch (IOException e) {
            log.error("IO exception occured during processing on raw video: {}", e.getMessage());
            throw new RuntimeException(e);

        } catch (InterruptedException e) {
            log.error("InterruptedException occured during processing on raw video: {}", e.getMessage());
            throw new RuntimeException(e);
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


    private void createAndPublishAddVideoToCourseEvent(VideoMetaData videoMetaData) {

        AddVideoToCourseEvent event = AddVideoToCourseEvent.builder()
                .videoPath(videoMetaData.randomVideoName())
                .videoDuration(videoMetaData.videoDuration())
                .courseId(videoMetaData.courseId())
                .displayName(videoMetaData.displayName())
                .build();

        kafkaPublisher.publishEvent(event);
    }

    private String generateRandomVideoName() {
        return UUID.randomUUID().toString();
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
