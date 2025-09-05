package com.example.final_projects.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ApiResult<T> {
    private boolean success;
    private int status;
    private String message;
    private String error;
    private T data;

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(HttpStatus.OK.getReasonPhrase())
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> ok(String message, T data) {
        return ApiResult.<T>builder()
                .success(true)
                .status(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResult<T> error(HttpStatus status, String errorCode, String message) {
        return ApiResult.<T>builder()
                .success(false)
                .status(status.value())
                .message(message)
                .error(errorCode)
                .data(null)
                .build();
    }
}
