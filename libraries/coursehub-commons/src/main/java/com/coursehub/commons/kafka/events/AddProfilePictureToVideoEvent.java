package com.coursehub.commons.kafka.events;

public record AddProfilePictureToVideoEvent(
        String videoId,
        String profilePictureName
) {
    public static Builder builder(){
        return new Builder();
    }

    public static class Builder {
        private String videoId;
        private String profilePictureName;

        public Builder videoId(String videoId) {
            this.videoId = videoId;
            return this;
        }

        public Builder profilePictureName(String profilePictureName) {
            this.profilePictureName = profilePictureName;
            return this;
        }

        public AddProfilePictureToVideoEvent build() {
            return new AddProfilePictureToVideoEvent(this.videoId, this.profilePictureName);
        }
    }
}
