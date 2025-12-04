package com.coursehub.commons.kafka.events;

public record DeleteVideoFromCourseEvent(
        String videoPath,
        String courseId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String videoPath;
        private String courseId;

        public Builder videoPath(String videoPath) {
            this.videoPath = videoPath;
            return this;
        }

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public DeleteVideoFromCourseEvent build() {
            return new DeleteVideoFromCourseEvent(videoPath, courseId);
        }
    }
}
