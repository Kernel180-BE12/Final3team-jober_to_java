package com.example.final_projects.controller;

import com.example.final_projects.dto.ApiResult;
import com.example.final_projects.dto.auth.*;
import com.example.final_projects.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    public AuthController(AuthService authService){ this.authService=authService; }

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
}
