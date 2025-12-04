package com.coursehub.commons.feign;

import java.io.Serializable;

public record UserResponse(
        String id,
        String username,
        String firstName,
        String lastName
) implements Serializable {

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .username(this.username)
                .firstName(this.firstName)
                .lastName(this.lastName);
    }

    public static class Builder {
        private String id;
        private String username;
        private String firstName;
        private String lastName;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public UserResponse build() {
            return new UserResponse(id, username, firstName, lastName);
        }
    }

}
