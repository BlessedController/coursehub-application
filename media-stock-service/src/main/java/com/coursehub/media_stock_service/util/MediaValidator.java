package com.coursehub.media_stock_service.util;

import com.coursehub.media_stock_service.exception.InvalidFileFormatException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

import static com.coursehub.media_stock_service.constants.FileConstants.*;


@Component
public class MediaValidator {
    protected String getValidExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null || originalFilename.isBlank()) {
            throw new InvalidFileFormatException("Invalid format of file");
        }

        return originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
    }

    public String getValidVideoExtension(MultipartFile file) {


        String extension = getValidExtension(file);

        Set<String> allowedExtensions = Set.of(MP4_EXTENSION, MOV_EXTENSION, AVI_EXTENSION);

        if (!allowedExtensions.contains(extension)) {
            throw new InvalidFileFormatException("Only video files(.mp4, .mov, .avi) are allowed");
        }

        return extension;
    }

    public String getValidPhotoExtension(MultipartFile file) {

        String extension = getValidExtension(file);

        Set<String> allowedExtensions = Set.of(PNG_EXTENSION, JPG_EXTENSION, JPEG_EXTENSION);

        if (!allowedExtensions.contains(extension)) {
            throw new InvalidFileFormatException("Only photo files(.png, .jpg, .jpeg) are allowed");
        }

        return extension;
    }

    public void validateProperty(String property) {
        if (property == null || property.isBlank()) {
            throw new InvalidFileFormatException("Property cannot be null or blank");
        }

        if (property.contains("..") || ILLEGAL_CHARACTERS_PATTERN.matcher(property).find()) {
            throw new InvalidFileFormatException("Invalid naming: contains illegal characters (.. \\\\ / : * ? \\\" < > |)");
        }
    }

    //TODO: MIME TYPE
    public boolean isVideoFile(Path path) {
        if (Files.isRegularFile(path)) {
            String name = path.getFileName().toString().toLowerCase();
            return name.endsWith(MP4_EXTENSION) || name.endsWith(MOV_EXTENSION) || name.endsWith(AVI_EXTENSION);
        }
        return false;
    }

    public boolean isPhotoFile(Path path) {
        if (Files.isRegularFile(path)) {
            String name = path.getFileName().toString().toLowerCase();
            return name.endsWith(PNG_EXTENSION) || name.endsWith(JPG_EXTENSION) || name.endsWith(JPEG_EXTENSION);
        }
        return false;
    }


}