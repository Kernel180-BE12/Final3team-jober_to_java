package com.example.final_projects.controller;

import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshResponse;
import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshRequest;
import org.springframework.http.ResponseEntity;
import com.example.final_projects.service.TokenServiceImpl;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class AuthTokenController {

    private final TokenServiceImpl tokenService;

    public AuthTokenController(TokenServiceImpl tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest req) {
        RefreshResponse res = tokenService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(res);
    }
}