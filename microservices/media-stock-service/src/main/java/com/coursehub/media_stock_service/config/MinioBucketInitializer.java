package com.coursehub.media_stock_service.config;

import io.minio.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MinioBucketInitializer implements CommandLineRunner {

    Logger log = LoggerFactory.getLogger(MinioBucketInitializer.class);

    private final MinioClient minioClient;

    @Value("${minio.user-profile-photo-bucket}")
    private String userProfilePicturesBucketName;

    @Value("${minio.course-profile-photo-bucket}")
    private String courseProfilePicturesBucketName;

    @Value("${minio.course-videos-bucket}")
    private String courseVideosBucketName;

    @Value("${minio.video-profile-photo-bucket}")
    private String videoProfilePicturesBucketName;

    @Override
    public void run(String... args) {
        this.createBucketIfNotExists(userProfilePicturesBucketName);
        this.createBucketIfNotExists(courseProfilePicturesBucketName);
        this.createBucketIfNotExists(courseVideosBucketName);
        this.createBucketIfNotExists(videoProfilePicturesBucketName);
    }

    private void createBucketIfNotExists(String bucketName) {
        try {

            boolean isUserProfileBucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().
                            bucket(bucketName)
                            .build()
            );

            if (!isUserProfileBucketExists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("Bucket created: {}", bucketName);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


}
