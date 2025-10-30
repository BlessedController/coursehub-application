package com.coursehub.commons.events.rating;

public record DeleteInstructorRatingEvent(
        String instructorId,
        Double rating
) {

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String instructorId;
        private Double rating;

        public Builder instructorId(String instructorId) {
            this.instructorId = instructorId;
            return this;
        }

        public Builder rating(Double rating) {
            this.rating = rating;
            return this;
        }

        public DeleteInstructorRatingEvent build() {
            return new DeleteInstructorRatingEvent(instructorId, rating);
        }
    }

}
