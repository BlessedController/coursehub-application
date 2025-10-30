package com.coursehub.media_stock_service.enums;

import lombok.Getter;
@Getter
public enum AllowedVideoMimeTypes {

    VIDEO_MP4("video/mp4", "mp4"),
    VIDEO_MPEG("video/mpeg", "mpeg"),
    VIDEO_AVI("video/avi", "avi"),;

    private final String mimeType;
    private final String extension;

    AllowedVideoMimeTypes(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public static boolean isVideoMimeTypeAllowed(String mimeType) {
        for (AllowedVideoMimeTypes allowed : values()) {
            if (allowed.mimeType.equalsIgnoreCase(mimeType)) {
                return true;
            }
        }
        return false;
    }


}
