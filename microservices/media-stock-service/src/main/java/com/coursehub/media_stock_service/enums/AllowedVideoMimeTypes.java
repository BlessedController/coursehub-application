package com.coursehub.media_stock_service.enums;

import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public enum AllowedVideoMimeTypes {

    VIDEO_MP4("video/mp4", "mp4"),
    VIDEO_MPEG("video/mpeg", "mpeg"),
    VIDEO_MOV("video/mov", "mov"),
    VIDEO_AVI("video/avi", "avi");

    private final String mimeType;
    private final String extension;

    AllowedVideoMimeTypes(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    private static final Set<String> ALLOWED_MIME_TYPES =
            Arrays.stream(values())
                    .map(AllowedVideoMimeTypes::getMimeType)
                    .collect(Collectors.toSet());


    public static boolean isVideoMimeTypeAllowed(String mimeType) {
        return ALLOWED_MIME_TYPES.contains(mimeType.toLowerCase());
    }

    public static Optional<AllowedVideoMimeTypes> fromExtension(String ext) {
        return Arrays.stream(values())
                .filter(v -> v.extension.equalsIgnoreCase(ext))
                .findFirst();
    }

}
