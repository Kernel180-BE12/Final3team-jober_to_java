package com.example.final_projects.service;

import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshResponse;


public interface TokenService {
   RefreshResponse refresh(String refreshToken);
}
