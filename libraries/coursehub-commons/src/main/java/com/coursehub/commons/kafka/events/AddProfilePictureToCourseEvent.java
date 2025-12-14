package com.coursehub.commons.kafka.events;

public record AddProfilePictureToCourseEvent(
        String courseId,
        String profilePictureName
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courseId;
        private String profilePictureName;

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder profilePictureName(String profilePictureName) {
            this.profilePictureName = profilePictureName;
            return this;
        }

        public AddProfilePictureToCourseEvent build() {
            return new AddProfilePictureToCourseEvent(this.courseId, this.profilePictureName);
        }
    }
}
