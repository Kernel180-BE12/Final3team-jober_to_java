package com.example.final_projects.service;

import com.example.final_projects.dto.auth.EmailOtpDtos.RequestOtpRequest;
import com.example.final_projects.dto.auth.EmailOtpDtos.VerifyOtpResponse;
import com.example.final_projects.dto.auth.EmailOtpDtos.VerifyOtpRequest;

public interface EmailOtpService {
    void requestOtp(RequestOtpRequest req);
    VerifyOtpResponse verifyOtp(VerifyOtpRequest req);
    void assertValidVerificationTokenForSignup(String email, String verificationToken);
}
