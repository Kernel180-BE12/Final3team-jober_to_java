package com.example.final_projects.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> {
    @Schema(description = "성공 시 데이터")
    private T data;
    @Schema(description = "성공 시 메시지")
    private String message;
    @Schema(description = "에러 정보")
    private ErrorResponse error;

    public static <T> ApiResult<T> ok(T data) {
        return ApiResult.<T>builder()
                .data(data)
                .message(null)
                .error(null)
                .build();
    }

    public static <T> ApiResult<T> ok(String message, T data) {
        return ApiResult.<T>builder()
                .data(data)
                .message(message)
                .error(null)
                .build();
    }

    public static <T> ApiResult<T> error(String code, String message) {
        return ApiResult.<T>builder()
                .data(null)
                .message(null)
                .error(ErrorResponse.of(code, message))
                .build();
    }
}
