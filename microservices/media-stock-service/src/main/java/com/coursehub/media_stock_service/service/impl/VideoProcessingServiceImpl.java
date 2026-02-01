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

        ProcessBuilder pb = this.createFFMPEGCommands(hlsTempDir, tempRawFile);

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

    /**
     ffmpeg \
     -i input.mp4 \
     -g 48 \
     -keyint_min 48 \
     -force_key_frames expr:gte(t,n_forced*2) \
     -filter:v:0 scale=w=1920:h=-2 -c:v:0 libx264 -b:v:0 3000k -c:a:0 aac -b:a:0 128k \
     -filter:v:1 scale=w=1280:h=-2 -c:v:1 libx264 -b:v:1 1800k -c:a:1 aac -b:a:1 128k \
     -filter:v:2 scale=w=854:h=-2  -c:v:2 libx264 -b:v:2 900k  -c:a:2 aac -b:a:2 96k \
     -filter:v:3 scale=w=640:h=-2  -c:v:3 libx264 -b:v:3 600k  -c:a:3 aac -b:a:3 96k \
     -map 0:v:0 -map 0:a:0 \
     -map 0:v:0 -map 0:a:0 \
     -map 0:v:0 -map 0:a:0 \
     -map 0:v:0 -map 0:a:0 \
     -f hls \
     -hls_time 2 \
     -hls_playlist_type vod \
     -hls_segment_filename /tmp/v%v/segment%d.ts \
     -master_pl_name master.m3u8 \
     -var_stream_map "v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3" \
     /tmp/v%v/playlist.m3u8
     */

    /**
     ffmpeg -i input.mp4 -g 48 -keyint_min 48 -force_key_frames expr:gte(t,n_forced*2) -filter:v:0 scale=w=1920:h=-2 -c:v:0 libx264 -b:v:0 3000k -c:a:0 aac -b:a:0 128k -filter:v:1 scale=w=1280:h=-2 -c:v:1 libx264 -b:v:1 1800k -c:a:1 aac -b:a:1 128k -filter:v:2 scale=w=854:h=-2 -c:v:2 libx264 -b:v:2 900k -c:a:2 aac -b:a:2 96k -filter:v:3 scale=w=640:h=-2 -c:v:3 libx264 -b:v:3 600k -c:a:3 aac -b:a:3 96k -map 0:v:0 -map 0:a:0 -map 0:v:0 -map 0:a:0 -map 0:v:0 -map 0:a:0 -map 0:v:0 -map 0:a:0 -f hls -hls_time 2 -hls_playlist_type vod -hls_segment_filename /tmp/v%v/segment%d.ts -master_pl_name master.m3u8 -var_stream_map "v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3" /tmp/v%v/playlist.m3u8
     */
    private ProcessBuilder createFFMPEGCommands(Path tempDir, Path rawVideoPath) {
        // FFmpeg komutlarını oluşturacak metot

        List<String> cmd = new ArrayList<>();
        // FFmpeg komutlarını sırayla eklemek için liste oluşturur

        cmd.add("ffmpeg");
        // Çalıştırılacak program: ffmpeg

        cmd.add("-i");
        // Input parametresi

        cmd.add(rawVideoPath.toString());
        // Input video dosyasının yolunu ekler

        cmd.add("-g");
        // GOP (Group of Pictures) uzunluğu parametresi

        cmd.add("48");
        // Her 48 frame’de bir keyframe koy

        cmd.add("-keyint_min");
        // Minimum keyframe aralığı parametresi

        cmd.add("48");
        // Minimum keyframe aralığı da 48 frame olsun

        cmd.add("-force_key_frames");
        // Zorla keyframe ekleme parametresi

        cmd.add("expr:gte(t,n_forced*2)");
        // Her 2 saniyede bir keyframe koy (HLS segmentleri düzgün kesilsin)

        this.addQuality(cmd, 0, 1920, "3000k", "128k");
        // 1. kalite: 1920px genişlik, video 3000k bitrate, audio 128k

        this.addQuality(cmd, 1, 1280, "1800k", "128k");
        // 2. kalite: 1280px genişlik, video 1800k bitrate, audio 128k

        this.addQuality(cmd, 2, 854, "900k", "96k");
        // 3. kalite: 854px genişlik, video 900k bitrate, audio 96k

        this.addQuality(cmd, 3, 640, "600k", "96k");
        // 4. kalite: 640px genişlik, video 600k bitrate, audio 96k

        for (int i = 0; i < 4; i++) {
            // Her kalite için video ve audio stream map edilecek

            cmd.add("-map");
            // Stream seçme parametresi

            cmd.add("0:v:0");
            // Input’un ilk video stream’ini seçer

            cmd.add("-map");
            // Stream seçme parametresi

            cmd.add("0:a:0");
            // Input’un ilk audio stream’ini seçer
        }

        cmd.add("-f");
        // Output format parametresi

        cmd.add("hls");
        // Çıktı formatı HLS olacak

        cmd.add("-hls_time");
        // Segment süresi parametresi

        cmd.add("2");
        // Her segment 2 saniye olacak

        cmd.add("-hls_playlist_type");
        // Playlist tipi parametresi

        cmd.add("vod");
        // Video on Demand playlist (video bitince kapanır)

        cmd.add("-hls_segment_filename");
        // Segment dosya isimlendirme parametresi

        cmd.add(tempDir.toString() + "/v%v/segment%d.ts");
        // Segmentler v0, v1, v2, v3 klasörlerine segment0.ts şeklinde yazılır

        cmd.add("-master_pl_name");
        // Master playlist adı parametresi

        cmd.add("master.m3u8");
        // Ana playlist dosyası master.m3u8 olacak

        cmd.add("-var_stream_map");
        // Çoklu kalite stream mapping parametresi

        cmd.add("v:0,a:0 v:1,a:1 v:2,a:2 v:3,a:3");
        // 4 video + 4 audio stream eşleştiriliyor

        cmd.add(tempDir + "/v%v/playlist.m3u8");
        // Her kalite için ayrı playlist oluşturulur: v0/playlist.m3u8 vb.

        ProcessBuilder pb = new ProcessBuilder(cmd);
        // Komut listesinden ProcessBuilder oluşturur

        pb.redirectErrorStream(true);
        // Error çıktısını da normal output içine yönlendirir

        return pb;
        // ProcessBuilder nesnesini döndürür
    }

    private void addQuality(List<String> cmd, int index, int width, String videoBitrate, String audioBitrate) {
        // Belirli bir kalite seviyesi ekleyen yardımcı metot

        cmd.add("-filter:v:" + index);
        // Video stream için filter parametresi

        cmd.add("scale=w=" + width + ":h=-2");
        // Videoyu belirtilen genişliğe ölçekler, yükseklik otomatik ayarlanır

        cmd.add("-c:v:" + index);
        // Video codec parametresi

        cmd.add("libx264");
        // Video codec olarak H.264 kullanılır

        cmd.add("-b:v:" + index);
        // Video bitrate parametresi

        cmd.add(videoBitrate);
        // Video bitrate değerini ekler (örn: 3000k)

        cmd.add("-c:a:" + index);
        // Audio codec parametresi

        cmd.add("aac");
        // Audio codec olarak AAC kullanılır

        cmd.add("-b:a:" + index);
        // Audio bitrate parametresi

        cmd.add(audioBitrate);
        // Audio bitrate değerini ekler (örn: 128k)
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
