package com.example.final_projects.dto.auth;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank private String refreshToken;
    public LogoutRequest(){}
    public LogoutRequest(String rt){ this.refreshToken=rt; }

    public String getRefreshToken() {
        return refreshToken;
    }
}
