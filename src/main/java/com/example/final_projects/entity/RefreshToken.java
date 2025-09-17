package com.example.final_projects.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="refresh_tokens",
       indexes = {
            @Index(name = "idx_refresh_tokens_jti", columnList = "jti"),
            @Index(name = "idx_refresh_tokens_replaced_by", columnList = "replaced_by_jti"),
            @Index(name = "idx_refresh_tokens_revoked", columnList = "revoked")
       },
        uniqueConstraints = {
            @UniqueConstraint(name = "uq_refresh_tokens_token_hash", columnNames = "token_hash")
        })
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash" , length=64)
    private String tokenHash;

    @Column(name="user_id", nullable = false)
    private Long userId;

    @Column(name="jti", length = 64)
    private String jti;

    @Column(name="replaced_by_jti", length = 64)
    private String replacedByJti;

    @Column(name="rotated_at")
    private LocalDateTime rotatedAt;

    @Column(nullable=false)
    private LocalDateTime expiresAt;

    @Column(name="created_at", insertable=false, updatable=false)
    private LocalDateTime createdAt;

    @Column(name="updated_at", insertable=false, updatable=false)
    private LocalDateTime updatedAt;

    @Column(nullable=false)
    private boolean revoked =false;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getTokenHash() { return tokenHash; }
    public void setTokenHash(String tokenHash) { this.tokenHash = tokenHash; }

    public String getJti() { return jti; }
    public void setJti(String jti) { this.jti = jti; }

    public String getReplacedByJti() { return replacedByJti; }
    public void setReplacedByJti(String replacedByJti) { this.replacedByJti = replacedByJti; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public LocalDateTime getRotatedAt() { return rotatedAt; }
    public void setRotatedAt(LocalDateTime rotatedAt) { this.rotatedAt = rotatedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

