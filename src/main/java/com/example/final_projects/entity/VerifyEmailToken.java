package com.example.final_projects.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_verification_token",
       uniqueConstraints = @UniqueConstraint(name="uq_evt_token", columnNames="token"))
public class VerifyEmailToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String token;

    @ManyToOne(fetch= FetchType.LAZY) @JoinColumn(name="user_id", nullable = true)
    private User user;

    @Column(length=255)
    private String email;

    @Column(nullable=false)
    private LocalDateTime expiresAt;

    @Column(nullable=false)
    private boolean used = false;

    @Column(nullable=false)
    private boolean preSignup = true;

    @Generated(GenerationTime.INSERT) // 선택: 하이버네이트에게 DB가 채운다고 힌트
    @Column(name="created_at", insertable = false, updatable = false)  // ★ 핵심
    private LocalDateTime createdAt;

    @Generated(GenerationTime.ALWAYS) // 선택
    @Column(name="updated_at", insertable = false, updatable = false)  // ★ 핵심
    private LocalDateTime updatedAt;

    public VerifyEmailToken() {

    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public VerifyEmailToken(Long id, String token, User user, LocalDateTime expiresAt, boolean used, String email) {
        this.id = id;
        this.token = token;
        this.user = user;
        this.expiresAt = expiresAt;
        this.used = used;
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isPreSignup() {
        return preSignup;
    }

    public void setPreSignup(boolean preSignup) {
        this.preSignup = preSignup;
    }
}
