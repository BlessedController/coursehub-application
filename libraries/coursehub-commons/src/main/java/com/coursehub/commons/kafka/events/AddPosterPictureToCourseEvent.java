package com.coursehub.commons.kafka.events;

public record AddPosterPictureToCourseEvent(
        String courseId,
        String posterPictureName
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courseId;
        private String posterPictureName;

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder posterPictureName(String posterPictureName) {
            this.posterPictureName = posterPictureName;
            return this;
        }

        public AddPosterPictureToCourseEvent build() {
            return new AddPosterPictureToCourseEvent(this.courseId, this.posterPictureName);
        }
    }
}
