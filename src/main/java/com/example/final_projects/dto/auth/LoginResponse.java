package com.example.final_projects.dto.auth;

public class LoginResponse {
    private String accessToken;
    private String refreshToken;
    public LoginResponse(){}
    public LoginResponse(String at, String rt){ this.accessToken=at; this.refreshToken=rt; }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
