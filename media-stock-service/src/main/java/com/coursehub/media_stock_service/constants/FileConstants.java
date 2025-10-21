package com.coursehub.media_stock_service.constants;

import java.util.regex.Pattern;

public class FileConstants {
    public static final String PNG_EXTENSION = ".png";
    public static final String JPG_EXTENSION = ".jpg";
    public static final String JPEG_EXTENSION = ".jpeg";

    public static final String MP4_EXTENSION = ".mp4";
    public static final String MOV_EXTENSION = ".mov";
    public static final String AVI_EXTENSION = ".avi";
    public static final String PROFILE_PHOTOS_PATH = "profile-photos";

    public static final Pattern ILLEGAL_CHARACTERS_PATTERN = Pattern.compile("[\\\\/:*?\"<>|]");

    public static final String VIDEO_FOLDER_NAME = "videos";
}
