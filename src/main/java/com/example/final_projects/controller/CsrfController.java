package com.example.final_projects.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CsrfController {

    @GetMapping("/api/auth/csrf")
    public Map<String, String> csrf(CsrfToken token) {
        // CookieCsrfTokenRepository.withHttpOnlyFalse() 설정 시
        // Set-Cookie: XSRF-TOKEN=<...> 도 함께 내려갑니다.
        return Map.of(
                "token", token.getToken(),
                "headerName", token.getHeaderName(),      // 보통 "X-CSRF-Token"
                "parameterName", token.getParameterName() // 폼 전송 시 사용할 이름
        );
    }
}
