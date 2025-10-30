package com.coursehub.media_stock_service;

import com.coursehub.commons.exceptions.FileOperationException;
import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MediaStockServiceApplication implements CommandLineRunner {
    Logger log = LoggerFactory.getLogger(MediaStockServiceApplication.class);

    @Value("${minio.profile-photo-bucket}")
    private String profilePhotoBucket;

    @Value("${minio.course-videos-bucket}")
    private String courseVideosBucket;

    private final MinioClient minioClient;

    public MediaStockServiceApplication(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public static void main(String[] args) {
        SpringApplication.run(MediaStockServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        createBucketIfNotExists(profilePhotoBucket);
        createBucketIfNotExists(courseVideosBucket);
    }


    private void createBucketIfNotExists(String bucketName) {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
                log.info("📦 Created new MinIO bucket [{}]", bucketName);
            } else {
                log.info("✅ MinIO bucket [{}] already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("❌ Error initializing MinIO bucket [{}]: {}", bucketName, e.getMessage());
            throw new FileOperationException("Error while creating bucket: " + bucketName);
        }
    }
}
