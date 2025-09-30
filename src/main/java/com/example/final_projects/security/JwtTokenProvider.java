package com.example.final_projects.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessValidityMillis;
    private final long refreshValidityMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret:}") String secret,                            // 기본값 허용(빈 문자열) 후 아래에서 검증
            @Value("${jwt.access-validity-ms:1800000}") long accessValidityMs, // 30분
            @Value("${jwt.refresh-validity-ms:1209600000}") long refreshValidityMs // 14일
    ) {
        this.key = buildKey(secret);               // ← 안전 검증 & 키 생성
        this.accessValidityMillis = accessValidityMs;
        this.refreshValidityMillis = refreshValidityMs;
    }

    private static Key buildKey(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException(
                    "jwt.secret is missing. Set env JWT_SECRET to a Base64-encoded 256-bit key (>=32 bytes after decode)."
            );
        }

        byte[] keyBytes;
        try {
            keyBytes = Base64.getDecoder().decode(secret);
        } catch (IllegalArgumentException decodeFail) {
            // 엄격 모드: Base64가 아니면 실패
            throw new IllegalStateException(
                    "jwt.secret must be Base64-encoded. Provide a 256-bit key (>=32 bytes after Base64 decode).", decodeFail
            );

            /*
            // (개발용 완화 모드) 주석 해제 시: Base64 해석 실패면 평문 길이 검증 후 사용
            // byte[] raw = secret.getBytes(StandardCharsets.UTF_8);
            // if (raw.length < 32) {
            //     throw new IllegalStateException("jwt.secret too short. Need >= 32 bytes. Use Base64 or longer plain text (DEV ONLY).");
            // }
            // keyBytes = raw;
            */
        }

        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "jwt.secret is too short after Base64 decode: need >= 32 bytes for HS256."
            );
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ===== Access Token =====

    public String createAccessToken(Long userId, String email, List<String> roles) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessValidityMillis);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("roles", roles)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getAccessValidityMillis() {
        return accessValidityMillis;
    }

    // ===== Refresh Token =====

    /** 기존 API(내부에서 JTI 생성) */
    public String createRefreshToken(Long userId) {
        return createRefreshToken(userId, UUID.randomUUID().toString());
    }

    /** 외부에서 생성한 JTI를 그대로 사용(회전 로직과 일치) */
    public String createRefreshToken(Long userId, String jti) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshValidityMillis);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setId(jti) // 표준 jti 클레임
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long getRefreshValidityMillis() {
        return refreshValidityMillis;
    }

    // ===== Parse / Helpers =====

    public Jws<Claims> parse(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token);
    }

    public Long getUserId(String token) {
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Object roles = parse(token).getBody().get("roles");
        if (roles == null) return List.of();
        if (roles instanceof List<?> l) return l.stream().map(String::valueOf).toList();
        return List.of(String.valueOf(roles));
    }

    public boolean isExpired(String token) {
        try {
            Date exp = parse(token).getBody().getExpiration();
            return exp.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }
}
