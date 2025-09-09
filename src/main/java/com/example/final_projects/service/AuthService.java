package com.example.final_projects.service;

import com.example.final_projects.dto.auth.*;

public interface AuthService {
    SignupResponse signup(SignupRequest request);
    LoginResponse login(LoginRequest request);
    void logout(LogoutRequest request);
    void verifyEmail(VerifyEmailRequest request);
}
