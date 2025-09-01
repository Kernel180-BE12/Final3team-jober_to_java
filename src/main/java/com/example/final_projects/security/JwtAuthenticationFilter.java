package com.example.final_projects.security;

import com.example.final_projects.entity.User;
import com.example.final_projects.repository.UserRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final AntPathMatcher matcher = new AntPathMatcher();

    // ⚠️ 실제 매핑에 따라 "/auth/**" 또는 "/api/auth/**" 로 맞추세요.
    private static final List<String> WHITELIST = List.of(
            "/auth/**",
            "/actuator/**"
    );

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository){
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 1) 화이트리스트는 무조건 통과
        String path = req.getServletPath(); // getRequestURI() 대신 이걸 권장
        boolean whitelisted = WHITELIST.stream().anyMatch(p -> matcher.match(p, path));
        if (whitelisted) {
            chain.doFilter(req, res);
            return;
        }

        // 2) Authorization 헤더가 없으면 통과 (익명 접근 여부는 Security가 판단)
        String auth = req.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer ")) {
            chain.doFilter(req, res);
            return;
        }

        // 3) Bearer 토큰이 있는데 잘못됐으면 401을 명확히 반환
        String token = auth.substring(7); // "Bearer " 이후
        try {
            var claims = jwtTokenProvider.parse(token).getBody();
            Long userId = Long.valueOf(claims.getSubject());

            var user = userRepository.findById(userId).orElse(null);
            if (user == null || user.getStatus() != User.Status.ACTIVE || user.isLocked()) {
                // 토큰은 맞지만 계정 상태가 비정상 → 인증 안 세우고 통과(익명)
                chain.doFilter(req, res);
                return;
            }

            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) claims.get("roles", List.class);

            var principal = new CustomUserPrincipal(user.getId(), user.getEmail(), user.getPasswordHash(), roles);
            var authToken = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authToken);
            chain.doFilter(req, res);
        } catch (JwtException | IllegalArgumentException e) {
            // ✅ 잘못된 토큰이면 401로 명확히 응답 (기존엔 조용히 무시해서 403로 이어졌음)
            res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            res.setContentType("application/json;charset=UTF-8");
            res.getWriter().write("{\"success\":false,\"message\":\"INVALID_TOKEN\"}");
        }
    }
}
