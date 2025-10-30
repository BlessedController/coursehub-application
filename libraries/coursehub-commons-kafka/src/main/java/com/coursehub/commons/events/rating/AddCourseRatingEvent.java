package com.coursehub.commons.events.rating;

public record AddCourseRatingEvent(
        String courseId,
        Double rating
) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String courseId;
        private Double rating;

        public Builder courseId(String courseId) {
            this.courseId = courseId;
            return this;
        }

        public Builder rating(Double rating) {
            this.rating = rating;
            return this;
        }

        public AddCourseRatingEvent build() {
            return new AddCourseRatingEvent(courseId, rating);
        }
    }
}
