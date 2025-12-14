package com.coursehub.commons.kafka.events;

public record AddProfilePictureToUserEvent(
        String userId,
        String profilePictureName
) {
    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String userId;
        private String profilePictureName;

        public Builder profilePictureName(String profilePictureName) {
            this.profilePictureName = profilePictureName;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AddProfilePictureToUserEvent build() {
            return new AddProfilePictureToUserEvent(this.userId, this.profilePictureName);
        }
    }
}
