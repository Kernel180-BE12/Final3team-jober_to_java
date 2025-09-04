package com.example.final_projects.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_template_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTemplateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Lob
    @Column(name = "request_content", columnDefinition = "LONGTEXT", nullable = false)
    private String requestContent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserTemplateRequestStatus status;

    @Column(name = "error_message", length = 500)
    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "userTemplateRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Template> templates = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = UserTemplateRequestStatus.PENDING;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
