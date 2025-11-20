package com.coursehub.commons.kafka.events;


public record DeleteUserProfilePhotoEvent(
        String userId
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String userId;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public DeleteUserProfilePhotoEvent build() {
            return new DeleteUserProfilePhotoEvent(userId);
        }
    }
}
