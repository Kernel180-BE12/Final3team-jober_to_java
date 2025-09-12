package com.example.final_projects.entity;

import jakarta.persistence.*;

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

    @Column(name="created_at", nullable=false, updatable=false)
    private LocalDateTime createdAt;

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
