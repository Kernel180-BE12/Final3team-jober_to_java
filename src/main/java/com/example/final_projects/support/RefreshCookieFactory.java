package com.example.final_projects.support;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class RefreshCookieFactory {

    @Value("${app.security.cookie.name:refresh}")
    private String name;

    @Value("${app.security.cookie.domain:}")
    private String domain;

    @Value("${app.security.cookie.same-site:Lax}") // Lax | None | Strict
    private String sameSite;

    @Value("${app.security.cookie.max-age-seconds:1209600}") // 14d
    private long maxAge;

    // 리프레시 재발급(/api/auth/token/refresh) + 로그아웃(/api/auth/logout) 모두 커버하려면 /api/auth 권장
    @Value("${app.security.cookie.path:/api/auth}")
    private String path;

    @Value("${app.security.cookie.secure:true}") // SameSite=None이면 secure 반드시 true 권장(브라우저 정책)
    private boolean secure;

    private String normalizedSameSite() {
        if (sameSite == null) return "Lax";
        switch (sameSite.trim().toLowerCase()) {
            case "none":   return "None";
            case "strict": return "Strict";
            case "lax":
            default:       return "Lax";
        }
    }

    public ResponseCookie buildIssueCookie(String token) {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, token)
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .maxAge(maxAge)
                .sameSite(normalizedSameSite());

        if (domain != null && !domain.isBlank()) b.domain(domain);
        return b.build();
    }

    public ResponseCookie buildExpireCookie() {
        ResponseCookie.ResponseCookieBuilder b = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .maxAge(0)
                .sameSite(normalizedSameSite());

        if (domain != null && !domain.isBlank()) b.domain(domain);
        return b.build();
    }

    /** 공통 유틸: HttpServletRequest에서 refresh 쿠키 값 읽기 (없으면 null) */
    public String read(HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equals(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }

    // 선택: 테스트/외부 사용 편의용
    public String getName() { return name; }
    public String getPath() { return path; }
}
