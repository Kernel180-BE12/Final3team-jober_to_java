package com.example.final_projects.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class SwaggerCsrfBridgeFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(SwaggerCsrfBridgeFilter.class);
    private static final Set<String> MUTATING = Set.of("POST","PUT","PATCH","DELETE");
    private static final String CSRF_HEADER = "X-CSRF-Token";
    private static final String CSRF_COOKIE = "XSRF-TOKEN";

    // Swagger가 넣어줄 수 있는 placeholder 값들(예시/기본값)
    private static final Set<String> PLACEHOLDERS = Set.of("string", "null", "undefined", "\"\"", "'");

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        final String path = req.getRequestURI();
        final String method = req.getMethod();

        if (!MUTATING.contains(method)) { chain.doFilter(req, res); return; }

        // 동일 출처 or Swagger UI에서 온 요청만 보정
        String origin = req.getHeader("Origin");
        String referer = req.getHeader("Referer");
        boolean sameOrigin = (origin == null) || isSameOrigin(req, origin);
        boolean fromSwagger = (referer != null && referer.contains("/swagger-ui/"));
        if (!(sameOrigin || fromSwagger)) { chain.doFilter(req, res); return; }

        // 요청에 실린 현재 헤더/쿠키
        String headerNow = trim(req.getHeader(CSRF_HEADER));
        String cookieVal = readCookie(req.getCookies(), CSRF_COOKIE);

        // 쿠키 없으면 보정 불가
        if (cookieVal == null || cookieVal.isBlank()) {
            log.debug("[CSRF-BRIDGE] {} {} → no {} cookie, pass-through", method, path, CSRF_COOKIE);
            chain.doFilter(req, res);
            return;
        }

        // 덮어쓰기 기준:
        //  - 헤더가 null/공백
        //  - placeholder 값("string" 등)
        //  - 헤더값이 쿠키와 불일치(잘못된 값) → 쿠키로 강제 덮어쓰기
        boolean shouldInject =
                (headerNow == null || headerNow.isBlank())
                        || PLACEHOLDERS.contains(headerNow)
                        || !cookieVal.equals(headerNow);

        if (!shouldInject) {
            log.debug("[CSRF-BRIDGE] {} {} → header already matches cookie, pass-through", method, path);
            chain.doFilter(req, res);
            return;
        }

        log.debug("[CSRF-BRIDGE] {} {} → injecting {} from cookie", method, path, CSRF_HEADER);
        final String xsrf = cookieVal;

        // getHeader / getHeaders / getHeaderNames 모두 덮어써서 하위 필터들이 확실히 보게 함
        HttpServletRequestWrapper wrapped = new HttpServletRequestWrapper(req) {
            @Override public String getHeader(String name) {
                if (CSRF_HEADER.equalsIgnoreCase(name)) return xsrf;
                return super.getHeader(name);
            }
            @Override public Enumeration<String> getHeaders(String name) {
                if (CSRF_HEADER.equalsIgnoreCase(name)) {
                    return Collections.enumeration(List.of(xsrf));
                }
                return super.getHeaders(name);
            }
            @Override public Enumeration<String> getHeaderNames() {
                Set<String> names = new LinkedHashSet<>(Collections.list(super.getHeaderNames()));
                names.add(CSRF_HEADER);
                return Collections.enumeration(names);
            }
        };

        chain.doFilter(wrapped, res);
    }

    private String readCookie(Cookie[] cookies, String name) {
        if (cookies == null) return null;
        for (Cookie c : cookies) {
            if (name.equalsIgnoreCase(c.getName())) {
                String v = c.getValue();
                return (v == null ? null : v.trim());
            }
        }
        return null;
    }

    private String trim(String s) { return s == null ? null : s.trim(); }

    private boolean isSameOrigin(HttpServletRequest req, String origin) {
        try {
            URI o = URI.create(origin);
            int originPort = (o.getPort() == -1)
                    ? ("https".equalsIgnoreCase(o.getScheme()) ? 443 : 80)
                    : o.getPort();
            return req.getScheme().equalsIgnoreCase(o.getScheme())
                    && req.getServerName().equalsIgnoreCase(o.getHost())
                    && req.getServerPort() == originPort;
        } catch (Exception e) {
            return false;
        }
    }
}
