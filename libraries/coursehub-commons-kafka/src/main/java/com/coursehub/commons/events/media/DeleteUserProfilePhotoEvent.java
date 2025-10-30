package com.coursehub.commons.events.media;


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
