package com.smarthospital.shared.dto;

import com.smarthospital.core.exception.ErrorResponse;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        Meta meta,
        ErrorResponse error
) {
    public record Meta(Instant timestamp) {}

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, new Meta(Instant.now()), null);
    }

    public static ApiResponse<Void> error(ErrorResponse error) {
        return new ApiResponse<>(false, null, new Meta(Instant.now()), error);
    }
}
