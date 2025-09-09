package com.example.final_projects.controller;

import com.example.final_projects.common.ApiResponse;
import com.example.final_projects.dto.auth.*;
import com.example.final_projects.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){ this.authService=authService; }

    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@Valid @RequestBody SignupRequest req){
        return ApiResponse.ok(authService.signup(req));
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest req){
        return ApiResponse.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody LogoutRequest req){
        authService.logout(req);
        return ApiResponse.ok("로그아웃 되었습니다.", null);
    }
    @GetMapping("/verify")
    public ApiResponse<Void> verify(@RequestParam("token") String token){
        authService.verifyEmail(new VerifyEmailRequest(token));
        return ApiResponse.ok("이메일 인증이 완료되었습니다.", null);
    }
}
