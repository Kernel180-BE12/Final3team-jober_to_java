package com.example.final_projects.exception.code;

import com.example.final_projects.exception.ErrorReason;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum TemplateErrorCode implements BaseErrorCode {
    INVALID_STATUS(HttpStatus.BAD_REQUEST.value(), "잘못된 상태 값입니다"),
    FORBIDDEN_STATUS(HttpStatus.BAD_REQUEST.value(), "허용되지 않은 상태 값입니다"),
    TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "템플릿을 찾을 수 없습니다"),
    ALREADY_APPROVE_REQUESTED(HttpStatus.BAD_REQUEST.value(), "이미 승인 요청된 템플릿입니다"),
    FORBIDDEN_TEMPLATE(HttpStatus.FORBIDDEN.value(), "권한이 없는 템플릿입니다"),
    APPROVE_REQUEST_FORBIDDEN(HttpStatus.BAD_REQUEST.value(), "승인 요청 가능한 상태가 아닙니다");

    private final ErrorReason errorReason;

    TemplateErrorCode(int status, String message) {
        this.errorReason = new ErrorReason(status, this.name(), message);
    }

    @Override
    public ErrorReason getErrorReason() {
        return errorReason;
    }
}
