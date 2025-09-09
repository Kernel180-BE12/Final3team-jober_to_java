package com.example.final_projects.entity;

import com.example.final_projects.dto.template.AiTemplateResponse;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Template {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id", nullable = false)
    private String categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_template_request_id")
    private UserTemplateRequest userTemplateRequest;

    private String title;

    @Lob
    private String content;

    @Enumerated(EnumType.STRING)
    private TemplateStatus status;

    @Enumerated(EnumType.STRING)
    private TemplateType type;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "reject_reason", length = 500)
    private String rejectReason;

    @Column(name = "reject_reason_summary", length = 500)
    private String rejectReasonSummary;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TemplateVariable> variables = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TemplateButton> buttons = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TemplateHistory> histories = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "template_industry",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "industry_id")
    )
    @Builder.Default
    private Set<Industry> industries = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "template_purpose",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "purpose_id")
    )
    @Builder.Default
    private Set<Purpose> purposes = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void updateFromAi(AiTemplateResponse ai) {
        this.categoryId = ai.categoryId();
        this.title = ai.title();
        this.content = ai.content();
        this.imageUrl = ai.imageUrl();
        this.type = TemplateType.valueOf(ai.type());
        this.isPublic = ai.isPublic();
        this.status = TemplateStatus.valueOf(ai.status());
        this.updatedAt = ai.updatedAt();
    }
}
