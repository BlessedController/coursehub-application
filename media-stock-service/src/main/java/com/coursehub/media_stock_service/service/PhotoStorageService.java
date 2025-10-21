package com.coursehub.media_stock_service.service;

import com.coursehub.media_stock_service.util.MediaValidator;
import com.coursehub.media_stock_service.exception.FileStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

import static com.coursehub.media_stock_service.constants.FileConstants.*;

@Service
public class PhotoStorageService {
    private final Path baseLocation;
    private final MediaValidator mediaValidator;

    public PhotoStorageService(@Value("${base.storage.location}") Path baseLocation,
                               MediaValidator mediaValidator) {
        this.baseLocation = baseLocation;
        this.mediaValidator = mediaValidator;
    }

    protected String storePhoto(MultipartFile file, String username, String extension) {
        Path targetDir = baseLocation.resolve(PROFILE_PHOTOS_PATH);

        try {
            Files.createDirectories(targetDir);
        } catch (IOException exception) {
            throw new FileStorageException("Failed to create directories for file storage");
        }

        String profilePhotoName = username + extension;

        Path finalDir = targetDir.resolve(profilePhotoName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, finalDir, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new FileStorageException("Failed to store the uploaded user profile photo");
        }

        return profilePhotoName;
    }

    protected void deletePhoto(String currentUsername) {
        Path targetDir1 = baseLocation.resolve(PROFILE_PHOTOS_PATH).resolve(currentUsername + PNG_EXTENSION);
        Path targetDir2 = baseLocation.resolve(PROFILE_PHOTOS_PATH).resolve(currentUsername + JPG_EXTENSION);
        Path targetDir3 = baseLocation.resolve(PROFILE_PHOTOS_PATH).resolve(currentUsername + JPEG_EXTENSION);

        Path photoPath;

        if (mediaValidator.isPhotoFile(targetDir1)) {
            photoPath = targetDir1;
        } else if (mediaValidator.isPhotoFile(targetDir2)) {
            photoPath = targetDir2;
        } else if (mediaValidator.isPhotoFile(targetDir3)) {
            photoPath = targetDir3;
        } else {
            throw new FileStorageException("Failed to find to delete the uploaded user profile photo");
        }

        try {
            Files.deleteIfExists(photoPath);
        } catch (IOException e) {
            throw new FileStorageException("Failed to delete the uploaded user profile photo");
        }

    }

}
