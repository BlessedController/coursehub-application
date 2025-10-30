package com.coursehub.commons.globals;

public record GlobalExceptionMessage(
        String timestamp,
        int statusCode,
        String reasonPhrase,
        String exceptionMessage,
        String uri
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        String timestamp;
        int statusCode;
        String reasonPhrase;
        String exceptionMessage;
        String uri;

        public Builder timestamp(String timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder statusCode(int statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder reasonPhrase(String reasonPhrase) {
            this.reasonPhrase = reasonPhrase;
            return this;
        }

        public Builder exceptionMessage(String exceptionMessage) {
            this.exceptionMessage = exceptionMessage;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public GlobalExceptionMessage build() {
            return new GlobalExceptionMessage(timestamp, statusCode, reasonPhrase, exceptionMessage, uri);
        }
    }

}
