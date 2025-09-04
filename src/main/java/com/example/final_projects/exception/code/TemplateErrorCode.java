package com.example.final_projects.exception.code;

import com.example.final_projects.exception.ErrorReason;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TemplateErrorCode implements BaseErrorCode {
    INVALID_STATUS(HttpStatus.BAD_REQUEST.value(), "잘못된 상태 값입니다"),
    FORBIDDEN_STATUS(HttpStatus.BAD_REQUEST.value(), "허용되지 않은 상태 값입니다");

    private final ErrorReason errorReason;

    TemplateErrorCode(int status, String message) {
        this.errorReason = new ErrorReason(status, this.name(), message);
    }

    @Override
    public ErrorReason getErrorReason() {
        return errorReason;
    }
}
