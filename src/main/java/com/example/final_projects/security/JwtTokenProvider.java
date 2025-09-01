package com.example.final_projects.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessValidityMillis;
    private final long refreshValidityMillis;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-validaity-ms:1800000}") long accessValidityMills,
            @Value("${jwt.refresh-validity-ms:1209600000}") long refreshValidityMillis
    ){
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.accessValidityMillis = accessValidityMills;
        this.refreshValidityMillis = refreshValidityMillis;
    }

    public String createAccessToken(Long userId, String email, List<String> roles){
        Date now = new Date();
        Date exp = new Date(now.getTime() + accessValidityMillis);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("roles", roles)
                .setIssuedAt(now).setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }

    public String createRefreshToken(Long userId){
        Date now = new Date();
        Date exp = new Date(now.getTime() + refreshValidityMillis);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(now).setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256).compact();
    }
    public Jws<Claims> parse(String token){
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    }
    public Long getUserId(String token){
        return Long.valueOf(parse(token).getBody().getSubject());
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token){
        Object roles = parse(token).getBody().get("roles");
        if(roles==null) return List.of();
        if (roles instanceof List<?> l) return l.stream().map(String::valueOf).toList();
        return List.of(String.valueOf(roles));
    }

    public boolean isExpired(String token){
        try{
            Date exp = parse(token).getBody().getExpiration();
            return exp.before(new Date());
        } catch (ExpiredJwtException e){
            return true;
        }

    }
}
