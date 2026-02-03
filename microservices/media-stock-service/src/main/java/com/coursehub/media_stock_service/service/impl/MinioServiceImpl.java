package com.coursehub.media_stock_service.service.impl;

import com.coursehub.commons.exceptions.FileOperationException;
import com.coursehub.media_stock_service.dto.VideoMetaData;
import com.coursehub.media_stock_service.service.MinioService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {

    private static final String FILE_SEPARATOR = "/";

    private final MinioClient minioClient;

    @Value("${minio.course-videos-bucket}")
    private String courseVideosBucketName;

    @Override
    public void uploadToMinio(VideoMetaData videoMetaData, Path tempDir) {

        String basePath = videoMetaData.contentCreatorId() + FILE_SEPARATOR + videoMetaData.courseId() + FILE_SEPARATOR + videoMetaData.randomVideoName() + FILE_SEPARATOR;

        try (Stream<Path> pathStream = Files.walk(tempDir)) {

            pathStream.filter(Files::isRegularFile).forEach(path -> {

                        String objectName = basePath +
                                tempDir.relativize(path).toString().replace("\\", FILE_SEPARATOR);

                        try (InputStream is = Files.newInputStream(path)) {

                            String fileName = path.getFileName().toString();

                            String contentType = "video/MP2T";

                            if (fileName.endsWith(".m3u8")) {
                                contentType = "application/x-mpegURL";
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
    }

}
