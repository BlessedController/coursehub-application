package com.coursehub.commons.kafka.events;

public record CourseRatingUpdatedEvent(
        String courseId,
        double averageRating,
        int ratingCount
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courseId;
        private double averageRating;
        private int ratingCount;

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder averageRating(double averageRating) {
            this.averageRating = averageRating;
            return this;
        }

        public Builder ratingCount(int ratingCount) {
            this.ratingCount = ratingCount;
            return this;
        }

        public CourseRatingUpdatedEvent build() {
            return new CourseRatingUpdatedEvent(courseId, averageRating, ratingCount);
        }
    }
}
