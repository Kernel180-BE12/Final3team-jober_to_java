package com.example.final_projects.exception.code;

import com.example.final_projects.exception.ErrorReason;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AiErrorCode implements BaseErrorCode {
    AI_REQUEST_FAILED(HttpStatus.BAD_GATEWAY.value(), "AI 서버 호출에 실패했습니다");

    private final ErrorReason errorReason;

    AiErrorCode(int status, String message) {this.errorReason = new ErrorReason(status, this.name(), message);}

    @Override
    public ErrorReason getErrorReason() { return errorReason; }
}
