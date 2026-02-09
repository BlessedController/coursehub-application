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

        Path hlsTempFolder = createFolderDir();

        Path tempRawFile = createTempVideoFile(file);

        ProcessBuilder process = createFFMPEGCommands(hlsTempFolder, tempRawFile);

        doProcessOnRawVideoFile(process);
        
        VideoMetaData videoMetaData =  createVideoMetaData(tempRawFile, displayName, principal.getId(), courseId);

        minioService.uploadToMinio(videoMetaData, hlsTempFolder);

        createAndPublishAddVideoToCourseEvent(videoMetaData);

        deleteHlsTempFolder(hlsTempFolder);
        deleteTempRawVideoFile(tempRawFile);
    }
    private VideoMetaData createVideoMetaData(Path tempRawFile, String displayName, String principalId, String courseId) {
        String randomVideoName = generateRandomVideoName();
        double videoDuration = getVideoDuration(tempRawFile);
        return new VideoMetaData(randomVideoName, displayName, principalId, courseId, videoDuration);
    }
    private void deleteTempRawVideoFile(Path tempRawFilePath) {
        if (tempRawFilePath == null) return;
        try {
            Files.deleteIfExists(tempRawFilePath);
        } catch (IOException e) {
            log.error("IO exception occured during deletion of temp raw file: {}", e.getMessage());
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

    private Path createFolderDir() {
        Path folderDir;
        try {
            folderDir = Files.createTempDirectory(HLS_STREAM_TEMP_DIR_NAME);
        } catch (IOException e) {
            log.error(" An IO Exception oocurred creating folder {}", e.getMessage());
            throw new RuntimeException();
        }
        return folderDir;
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


    private void doProcessOnRawVideoFile(ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();

            consumeProcessLogs(process);

            int exitCode = process.waitFor();

            if (exitCode != 0) {
                log.error("FFMPEG command failed with exit code {}", exitCode);
                throw new RuntimeException();
            }
        } catch (IOException e) {
            log.error("IO exception occured during processing on raw video: {}", e.getMessage());
            throw new RuntimeException();

        } catch (InterruptedException e) {
            log.error("InterruptedException occured during processing on raw video: {}", e.getMessage());
            throw new RuntimeException();
        }
    }

    private void consumeProcessLogs(Process process) {
        new Thread(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    log.info(line);
                }
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }).start();
    }

    private void addQuality(int index, String videoBitrate, String audioBitrate, int width, List<String> cmd) {

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

    private ProcessBuilder createFFMPEGCommands(Path hlsTempDir, Path tempRawFile) {

        List<String> cmd = new ArrayList<>();

        cmd.add("ffmpeg");
        cmd.add("-threads");
        cmd.add("2");

        cmd.add("-i");
        cmd.add(tempRawFile.toString());

        cmd.add("-g");
        cmd.add("48");
        cmd.add("-keyint_min");
        cmd.add("48");
        cmd.add("-force_key_frames");
        cmd.add("expr:gte(t,n_forced*2)");

        for (int i = 0; i < 4; i++) {
            cmd.add("-map");
            cmd.add("0:v:0");
            cmd.add("-map");
            cmd.add("0:a:0");
        }

        addQuality(0, "600k", "96k", 640, cmd);
        addQuality(1, "900k", "96k", 854, cmd);
        addQuality(2, "1800k", "128k", 1280, cmd);
        addQuality(3, "3000k", "128k", 1920, cmd);

        cmd.add("-f");
        cmd.add("hls");

        cmd.add("-hls_time");
        cmd.add("2");

        cmd.add("-hls_playlist_type");
        cmd.add("vod");

        cmd.add("-var_stream_map");
        cmd.add("v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3");

        cmd.add("-hls_segment_filename");
        cmd.add(hlsTempDir.resolve("v%v/segment%d.ts").toString());

        cmd.add("-master_pl_name");
        cmd.add("master.m3u8");
        cmd.add(hlsTempDir.resolve("v%v/playlist.m3u8").toString());


        ProcessBuilder pb = new ProcessBuilder(cmd);

        pb.redirectErrorStream(true);

        return pb;
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


    private void deleteHlsTempFolder(Path folder) {
        if (folder == null) {
            return;
        }

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
