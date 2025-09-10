package com.example.final_projects.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class VerifyEmailRequest {
    @NotBlank
    private String token;
    public VerifyEmailRequest(){}

    public VerifyEmailRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
