package com.example.final_projects.exception;

import com.example.final_projects.exception.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class AiException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public AiException(BaseErrorCode errorCode) {
        super(errorCode.getErrorReason().getMessage());
        this.errorCode = errorCode;
    }

    public AiException(BaseErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AiException(BaseErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
}
