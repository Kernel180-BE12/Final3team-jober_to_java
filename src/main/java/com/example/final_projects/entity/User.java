package com.example.final_projects.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=10)
    private Status status = Status.PENDING;

    @Column(nullable=false)
    private boolean locked = false;

    private LocalDateTime lockedAt;
    private LocalDateTime lastLoginAt;

    @Column(nullable=false)
    private int failCount = 0;

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="user_role_mapping",
            joinColumns=@JoinColumn(name="user_id"),
            inverseJoinColumns=@JoinColumn(name="role_id"))
    private Set<Role> roles = new HashSet<>();

    @CreatedDate
    @Column(nullable = false, updatable=false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable=false)
    private LocalDateTime updatedAt;

    public User() {

    }


    public enum Status{PENDING, ACTIVE, INACTIVE}



    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (!(o instanceof User other)) return false;
        return this.id != null && this.id.equals(other.id);
    }

    public User(Long id, String email, String passwordHash, String name, Status status, boolean locked, LocalDateTime lockedAt, LocalDateTime lastLoginAt, int failCount, Set<Role> roles, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.name = name;
        this.status = status;
        this.locked = locked;
        this.lockedAt = lockedAt;
        this.lastLoginAt = lastLoginAt;
        this.failCount = failCount;
        this.roles = roles;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @Override
    public int hashCode() {
        return (id != null) ? id.hashCode() : 0;
    }

    public long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }

    public boolean isLocked() {
        return locked;
    }

    public LocalDateTime getLockedAt() {
        return lockedAt;
    }

    public LocalDateTime getLastLoginAt() {
        return lastLoginAt;
    }

    public int getFailCount() {
        return failCount;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
