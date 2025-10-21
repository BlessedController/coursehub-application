package com.coursehub.media_stock_service.service;


import com.coursehub.media_stock_service.util.MediaValidator;
import com.coursehub.media_stock_service.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.coursehub.media_stock_service.constants.FileConstants.VIDEO_FOLDER_NAME;

@Service
public class VideoStorageService {
    private final Path baseLocation;
    private final MediaValidator mediaValidator;

    public VideoStorageService(@Value("${base.storage.location}") Path baseLocation, MediaValidator mediaValidator) {
        this.baseLocation = baseLocation;
        this.mediaValidator = mediaValidator;
    }

    protected void deletePathIfEmpty(Path path) {
        List<Path> regularVideos = getRegularVideos(path);

        if (Files.isDirectory(path) && regularVideos.isEmpty()) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new FileStorageException("Failed to delete folder");
            }
        }
    }

    protected List<Path> getRegularVideos(Path path) {
        try (Stream<Path> pathStream = Files.walk(path)) {
            return pathStream
                    .filter(mediaValidator::isVideoFile)
                    .toList();
        } catch (IOException e) {
            throw new FileStorageException("Failed to read video files");
        }
    }

    protected String storeVideo(MultipartFile file, String subFolder) {
        Path targetDir = baseLocation.resolve(subFolder);

        if (!Files.isDirectory(targetDir)) {
            try {
                Files.createDirectories(targetDir);
            } catch (IOException exception) {
                throw new FileStorageException("Failed to create directories for file storage");
            }
        }

        String extension = mediaValidator.getValidVideoExtension(file);
        String filename = UUID.randomUUID() + extension;
        Path finalDir = targetDir.resolve(filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, finalDir, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new FileStorageException("Failed to store the uploaded file");
        }

        return filename;
    }

    protected void deleteVideo(String instructorName, String courseId, String filename) {

        Path filePath = Paths.get(baseLocation.toString(),
                VIDEO_FOLDER_NAME,
                instructorName,
                courseId,
                filename);

        if (Files.isRegularFile(filePath)) {
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new FileStorageException("File cannot delete: " + filename);
            }
        } else {
            throw new FileStorageException("File not found or is not a valid file: " + filename);
        }

        //TODO: SİLİNMƏ SIRASINI DƏYİŞ NƏ OLACAĞINI GÖR. ALT FOLDERLER DOLU HALDA SİLİNƏCƏKMİ İNSTRUCTOR SİLİNƏRKƏN.
        Path pathUntilCourse = baseLocation.resolve(VIDEO_FOLDER_NAME).resolve(instructorName).resolve(courseId);
        deletePathIfEmpty(pathUntilCourse);

        Path pathUntilInstructor = baseLocation.resolve(VIDEO_FOLDER_NAME).resolve(instructorName);
        deletePathIfEmpty(pathUntilInstructor);
    }

    public Boolean checkVideoExists(String instructorName, String courseId, String filename) {
        Path targetDir = baseLocation.resolve(VIDEO_FOLDER_NAME).resolve(instructorName).resolve(courseId).resolve(filename);
        return mediaValidator.isVideoFile(targetDir);
    }
}
