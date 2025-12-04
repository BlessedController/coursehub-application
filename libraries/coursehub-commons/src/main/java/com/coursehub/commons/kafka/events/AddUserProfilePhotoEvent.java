package com.coursehub.commons.kafka.events;

public record AddUserProfilePhotoEvent(
        String profilePhotoName,
        String userId
) {
    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private String profilePhotoName;
        private String userId;

        public Builder profilePhotoName(String profilePhotoName) {
            this.profilePhotoName = profilePhotoName;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public AddUserProfilePhotoEvent build() {
            return new AddUserProfilePhotoEvent(profilePhotoName, userId);
        }
    }
}
