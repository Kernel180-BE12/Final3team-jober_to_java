package com.example.final_projects.controller;

import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshRequest;
import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshResponse;
import com.example.final_projects.service.TokenServiceImpl;
import com.example.final_projects.support.RefreshCookieFactory;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Refresh/Logout: 쿠키 우선, 바디는 하위호환 백업
 * - POST 고정 (CSRF 보호 대상)
 * - 새 RT는 HttpOnly 쿠키로 재발급
 */
@RestController
@RequestMapping("/api/auth")
public class AuthTokenController {

    private final TokenServiceImpl tokenService;
    private final RefreshCookieFactory cookieFactory;

    public AuthTokenController(TokenServiceImpl tokenService,
                               RefreshCookieFactory cookieFactory) {
        this.tokenService = tokenService;
        this.cookieFactory = cookieFactory;
    }

    /** RT 재발급: 쿠키 우선 + 바디 백업(하위호환) */
    @PostMapping("/token/refresh")
    public ResponseEntity<RefreshResponse> refresh(
            @RequestBody(required = false) RefreshRequest req,
            HttpServletRequest httpReq
    ) {
        // 1) 쿠키에서 refresh 우선 읽기
        String rt = cookieFactory.read(httpReq);

        // 2) 바디 백업(이전 클라이언트 호환)
        if (rt == null && req != null) {
            rt = req.getRefreshToken();
        }

        //2.5) 누락가드
        if (rt == null || rt.isBlank()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token is missing");
        }

        // 회전/검증은 기존 서비스 로직 사용 (재사용 감지 포함)
        RefreshResponse res = tokenService.refresh(rt);

        // 새 RT를 쿠키로 재발급
        ResponseCookie cookie = cookieFactory.buildIssueCookie(res.getRefreshToken());

        // 바디에 RT를 빼고 싶다면 DTO에 withoutRefreshToken() 같은 메서드가 있어야 함.
        // 없으면 그대로 반환하거나, 서버에서 제외된 전용 DTO를 사용.
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(res /* .withoutRefreshToken() */);
    }
}
