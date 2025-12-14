package com.coursehub.media_stock_service.enums;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum AllowedPhotoMimeTypes {

    IMAGE_JPG("image/jpg", "jpg"),
    IMAGE_JPEG("image/jpeg", "jpeg"),
    IMAGE_PNG("image/png", "png");

    private final String mimeType;
    private final String extension;

    AllowedPhotoMimeTypes(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }


    private static final Set<String> ALLOWED_MIME_TYPES =
            Arrays.stream(values())
                    .map(AllowedPhotoMimeTypes::getMimeType)
                    .collect(Collectors.toSet());


    public static boolean isVideoMimeTypeAllowed(String mimeType) {
        return ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    public static Optional<AllowedPhotoMimeTypes> fromExtension(String ext) {
        return Arrays.stream(values())
                .filter(v -> v.extension.equalsIgnoreCase(ext))
                .findFirst();
    }
}
