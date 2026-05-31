package com.smarthospital.core.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String code,
        String message,
        Map<String, String> details,
        Instant timestamp
) {
    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String              code;
        private String              message;
        private Map<String, String> details;
        private Instant             timestamp;

        public Builder code(String v)              { this.code      = v; return this; }
        public Builder message(String v)           { this.message   = v; return this; }
        public Builder details(Map<String, String> v) { this.details = v; return this; }
        public Builder timestamp(Instant v)        { this.timestamp = v; return this; }
        public ErrorResponse build() {
            return new ErrorResponse(code, message, details, timestamp);
        }
    }
}
