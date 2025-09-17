package com.example.final_projects.service;

import com.example.final_projects.dto.auth.RefreshTokenDtos.RefreshResponse;
import com.example.final_projects.entity.RefreshToken;
import com.example.final_projects.repository.RefreshTokenRepository;
import com.example.final_projects.security.JwtTokenProvider;
import com.example.final_projects.security.TokenHashUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class TokenServiceImpl implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final String pepper;
    private final long refreshValidityMs;

    public TokenServiceImpl(RefreshTokenRepository refreshTokenRepository,
                            JwtTokenProvider jwtTokenProvider,
                            @Value("${security.refresh.pepper}") String pepper,
                            @Value("${security.refresh.validity-ms:1209600000}") long refreshValidityMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.pepper = pepper;
        this.refreshValidityMs = refreshValidityMs;
    }

    @Override
    public RefreshResponse refresh(String refreshTokenRaw) {
        // 1) 제출된 RT 해시화 → 조회
        String hash = TokenHashUtil.sha256HexWithPepper(pepper, refreshTokenRaw);
        RefreshToken current = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        // 2) 재사용/만료 차단
        if (current.isRevoked() || current.getReplacedByJti() != null) {
            throw new IllegalStateException("Refresh token reuse detected");
        }
        if (current.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Refresh token expired");
        }

        // 3) 조건부 회전(동시성 제어)
        String newJti = UUID.randomUUID().toString();
        int updated = refreshTokenRepository.markRotated(current.getJti(), newJti);
        if (updated != 1) {
            throw new IllegalStateException("Refresh token reuse detected");
        }

        // 4) 새 AT/RT 발급
        Long userId = current.getUserId();
        String accessToken = jwtTokenProvider.createAccessToken(userId, null, List.of("ROLE_USER"));
        String newRefreshToken = jwtTokenProvider.createRefreshToken(userId, newJti);

        // 5) 새 RT 저장(해시만 저장)
        String newHash = TokenHashUtil.sha256HexWithPepper(pepper, newRefreshToken);
        LocalDateTime newExp = LocalDateTime.now().plus(Duration.ofMillis(refreshValidityMs));

        RefreshToken next = new RefreshToken();
        next.setUserId(userId);
        next.setJti(newJti);
        next.setTokenHash(newHash);
        next.setExpiresAt(newExp);
        next.setRevoked(false);
        refreshTokenRepository.save(next);

        // 6) 응답
        long atExpMs = jwtTokenProvider.getAccessValidityMillis();
        long rtExpMs = refreshValidityMs;
        return new RefreshResponse(accessToken, newRefreshToken, atExpMs, rtExpMs);
    }
}
