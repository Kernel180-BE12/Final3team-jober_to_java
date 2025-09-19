package com.example.final_projects.controller;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.dto.auth.EmailOtpDtos;
import com.example.final_projects.service.EmailOtpService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/email/otp")
public class EmailOtpController {
    private final EmailOtpService emailOtpService;

    public EmailOtpController(EmailOtpService emailOtpService) {
        this.emailOtpService = emailOtpService;
    }

    /** OTP 발송 요청 */
    @PostMapping("/request")
    public ApiResult<Void> request(@Valid @RequestBody EmailOtpDtos.RequestOtpRequest req) {
        emailOtpService.requestOtp(req);
        return ApiResult.ok("인증 코드가 전송되었습니다.", null);
    }

    /** OTP 검증 → pre-signup 검증토큰 발급 */
    @PostMapping("/verify")
    public ApiResult<EmailOtpDtos.VerifyOtpResponse> verify(@Valid @RequestBody EmailOtpDtos.VerifyOtpRequest req) {
        var resp = emailOtpService.verifyOtp(req);
        return ApiResult.ok("인증 코드가 확인되었습니다.", resp);
    }
}
