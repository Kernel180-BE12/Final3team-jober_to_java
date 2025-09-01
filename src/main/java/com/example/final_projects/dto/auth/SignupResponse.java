package com.example.final_projects.dto.auth;

public class SignupResponse {
    private Long userId;
    private String message;
    public SignupResponse(){}
    public SignupResponse(Long id, String msg){ this.userId=id; this.message=msg; }

    public Long getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }
}
