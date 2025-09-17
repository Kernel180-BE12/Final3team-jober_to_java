package com.example.final_projects.controller;

import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshResponse;
import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshRequest;
import com.example.final_projects.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Ref;

@RestController
@RequestMapping("/auth")
public class AuthTokenController {

    private final TokenService tokenService;

    public AuthTokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest req) {
        RefreshResponse res = tokenService.refresh(req.getRefreshToken());
        return ResponseEntity.ok(res);
    }
}

