package com.example.final_projects.exception;

import com.example.final_projects.exception.code.BaseErrorCode;
import lombok.Getter;

@Getter
public class TemplateException extends RuntimeException {
    private final BaseErrorCode errorCode;

    public TemplateException(BaseErrorCode errorCode) {
        super(errorCode.getErrorReason().getMessage());
        this.errorCode = errorCode;
    }
}
