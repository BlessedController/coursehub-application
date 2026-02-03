package com.coursehub.media_stock_service.service;

import com.coursehub.media_stock_service.dto.VideoMetaData;

import java.nio.file.Path;

public interface MinioService {
    void uploadToMinio(VideoMetaData videoMetaData, Path tempDir);
}
