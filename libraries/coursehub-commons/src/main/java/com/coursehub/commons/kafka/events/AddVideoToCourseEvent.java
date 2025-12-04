package com.coursehub.commons.kafka.events;

public record AddVideoToCourseEvent(
        String videoPath,
        String displayName,
        String courseId
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String videoPath;
        private String displayName;
        private String courseId;

        public Builder videoPath(String videoPath) {
            this.videoPath = videoPath;
            return this;
        }

        public Builder displayName(String displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public AddVideoToCourseEvent build() {
            return new AddVideoToCourseEvent(videoPath, displayName, courseId);
        }
    }

}
