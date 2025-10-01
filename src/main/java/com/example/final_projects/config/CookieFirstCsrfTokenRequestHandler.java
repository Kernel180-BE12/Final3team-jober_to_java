package com.example.final_projects.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;

import java.util.function.Supplier;

/**
 * Swagger가 X-CSRF-Token 헤더에 빈값/placeholder를 보내도
 * 항상 쿠키(XSRF-TOKEN) 값을 우선 사용하도록 하는 핸들러.
 * 상속 금지(final)인 XorCsrfTokenRequestAttributeHandler는 "구성"으로 사용.
 */
@Component
public class CookieFirstCsrfTokenRequestHandler implements CsrfTokenRequestHandler {

    private static final String CSRF_COOKIE = "XSRF-TOKEN";

    // Spring Security 6 기본 핸들러(마스킹 처리 포함)를 delegate로 사용
    private final CsrfTokenRequestHandler delegate = new XorCsrfTokenRequestAttributeHandler();

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       Supplier<CsrfToken> csrfToken) {
        // 표준 동작 유지 (요청 속성에 토큰 심기 등)
        delegate.handle(request, response, csrfToken);
    }

    @Override
    public String resolveCsrfTokenValue(HttpServletRequest request, CsrfToken csrfToken) {
        // 1) 쿠키에서 먼저 찾는다
        String fromCookie = readCookie(request, CSRF_COOKIE);
        if (StringUtils.hasText(fromCookie)) {
            // 쿠키 값이 있으면 그걸 사용 (delegate가 내부적으로 XOR 언마스킹 처리 가능)
            return fromCookie.trim();
        }
        // 2) 쿠키가 없을 때만 기본(header/param) 로직으로 폴백
        return delegate.resolveCsrfTokenValue(request, csrfToken);
    }

    private String readCookie(HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equalsIgnoreCase(c.getName())) {
                return c.getValue();
            }
        }
        return null;
    }
}
