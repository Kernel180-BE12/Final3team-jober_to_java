package com.example.final_projects.controller;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.dto.auth.*;
import com.example.final_projects.service.AuthServiceImpl;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    public AuthController(AuthServiceImpl authService){ this.authService=authService; }

    @PostMapping("/signup")
    public ApiResult<SignupResponse> signup(@Valid @RequestBody SignupRequest req){
        return ApiResult.ok(authService.signup(req));
    }

    @PostMapping("/login")
    public ApiResult<LoginResponse> login(@Valid @RequestBody LoginRequest req){
        return ApiResult.ok(authService.login(req));
    }

    @PostMapping("/logout")
    public ApiResult<Void> logout(@Valid @RequestBody LogoutRequest req){
        authService.logout(req);
        return ApiResult.ok("로그아웃 되었습니다.", null);
    }
    @GetMapping("/verify")
    public ApiResult<Void> verify(@RequestParam("token") String token){
        authService.verifyEmail(new VerifyEmailRequest(token));
        return ApiResult.ok("이메일 인증이 완료되었습니다.", null);
    }
}
