package com.example.final_projects.exception;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.dto.ErrorResponse;
import com.example.final_projects.exception.code.BaseErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private <T> ResponseEntity<ApiResult<T>> buildErrorResponse(HttpStatus status, String code, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResult.<T>builder()
                        .data(null)
                        .error(ErrorResponse.of(code, message))
                        .build()
                );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResult<Object>> handleIllegalArgument(IllegalArgumentException ex) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResult<Object>> handleAccessDenied(AccessDeniedException ex) {
        return buildErrorResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", ex.getMessage());
    }

    @ExceptionHandler({IllegalStateException.class, NullPointerException.class})
    public ResponseEntity<ApiResult<Object>> handleServerErrors(RuntimeException ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResult<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().getFirst().getDefaultMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResult<Object>> handleGeneral(Exception ex) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "UNEXPECTED_ERROR", ex.getMessage());
    }

    @ExceptionHandler(TemplateException.class)
    public ResponseEntity<ApiResult<Object>> handleTemplateException(TemplateException ex) {
        BaseErrorCode errorCode = ex.getErrorCode();
        return buildErrorResponse(
                HttpStatus.valueOf(errorCode.getErrorReason().getStatus()),
                errorCode.getErrorReason().getCode(),
                errorCode.getErrorReason().getMessage()
        );
    }
}
