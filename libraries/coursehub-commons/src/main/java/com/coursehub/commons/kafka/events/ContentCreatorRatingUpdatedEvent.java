package com.coursehub.commons.kafka.events;

public record ContentCreatorRatingUpdatedEvent(
        String contentCreatorId,
        double averageRating,
        int ratingCount

) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String contentCreatorId;
        private double averageRating;
        private int ratingCount;

        public Builder instructorId(String contentCreatorId) {
            this.contentCreatorId = contentCreatorId;
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

        public ContentCreatorRatingUpdatedEvent build() {
            return new ContentCreatorRatingUpdatedEvent(contentCreatorId, averageRating, ratingCount);
        }
    }
}
