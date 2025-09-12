package com.example.final_projects.dto.auth;

public class EmailOtpDtos {
    public record RequestOtpRequest(String email) {}
    public record VerifyOtpRequest(String email, String code) {}
    public record VerifyOtpResponse(String verificationToken, long expiresInSeconds) {}
}
