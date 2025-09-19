package com.example.final_projects.exception.user;

import org.springframework.http.HttpStatus;

public enum UserErrorCode {

//회원가입, 이메일
EMAIL_DUPLICATE(HttpStatus.CONFLICT, "이미 사용중인 이메일입니다."),

EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),

OTP_INVALID(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 코드입니다."),

OTP_EXPIRED(HttpStatus.BAD_REQUEST, "인증 코드가 만료되었습니다."),

OTP_COOLDOWN(HttpStatus.TOO_MANY_REQUESTS, "인증 코드 재요청 쿨다운 중입니다."),

//로그인, 인증
LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),

ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "계정이 잠겼습니다."),

USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

AUTH_REQUIRED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),

// 토큰 / RT 로테이션
TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),

REFRESH_TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "리프레시 토큰이 존재하지 않습니다."),

REFRESH_TOKEN_REUSED(HttpStatus.UNAUTHORIZED, "재사용이 감지된 리프레시 토큰입니다."),

REFRESH_TOKEN_ROTATION_FAILED(HttpStatus.UNAUTHORIZED, "리프레시 토큰 로테이션에 실패했습니다."),

//검증/ 기타
VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),

RATE_LIMIT_EXCEEDED(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다."),

INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

private final HttpStatus status;
private final String defaultMessage;

UserErrorCode(HttpStatus status, String defaultMessage) {
    this.status = status;
    this.defaultMessage = defaultMessage;
}

public HttpStatus getStatus() {
    return status;
}

public String getDefaultMessage() {
    return defaultMessage;
}
public String code() { return "USER." + name();}
}