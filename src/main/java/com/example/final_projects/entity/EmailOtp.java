package com.example.final_projects.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_otp",
        indexes = {
            @Index(name="idx_email_otp_email", columnList="email"),
            @Index(name="idx_email_otp_expires", columnList="expires_at"),
            @Index(name="idx_email_otp_pending", columnList="email, verified, expires_at")
        })
public class EmailOtp {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=255)
    private String email;

    @Column(name="code_hash", nullable=false, length=255)
    private String codeHash;

    @Column(name="expires_at", nullable=false)
    private LocalDateTime expiresAt;

    @Column(nullable=false)
    private boolean verified = false;

    @Column(name="attempt_count", nullable=false)
    private int attemptCount = 0;

    @Column(name="last_sent_at", nullable=false)
    private LocalDateTime lastSentAt;

    @Column(name="resend_count", nullable=false)
    private int resendCount = 0;

    @Column(name="created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name="updated_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCodeHash() {
        return codeHash;
    }

    public void setCodeHash(String codeHash) {
        this.codeHash = codeHash;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public int getAttemptCount() {
        return attemptCount;
    }

    public void setAttemptCount(int attemptCount) {
        this.attemptCount = attemptCount;
    }

    public LocalDateTime getLastSentAt() {
        return lastSentAt;
    }

    public void setLastSentAt(LocalDateTime lastSentAt) {
        this.lastSentAt = lastSentAt;
    }

    public int getResendCount() {
        return resendCount;
    }

    public void setResendCount(int resendCount) {
        this.resendCount = resendCount;
    }

    public Long getId() {
        return id;
    }
}
