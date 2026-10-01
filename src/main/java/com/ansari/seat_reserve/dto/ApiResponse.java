package com.ansari.seat_reserve.dto;

import java.time.Instant;

public record ApiResponse<T>(int code, boolean success, String message, T data, Instant timestamp) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(200, true, "Success", data, Instant.now());
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, true, "Created", data, Instant.now());
    }

    public static <T> ApiResponse<T> of(int code, String message, T data) {
        return new ApiResponse<>(code, true, message, data, Instant.now());
    }
}
