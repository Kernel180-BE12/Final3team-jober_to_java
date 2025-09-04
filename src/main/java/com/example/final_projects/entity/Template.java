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
    private Long categoryId;

    private String title;

    @Lob
    private String content;

    @Column(name = "request_content")
    private String requestContent;

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
    private List<TemplateVariable> variables = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateButton> buttons = new ArrayList<>();

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateHistory> histories = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "template_industry",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "industry_id")
    )
    private Set<Industry> industries = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "template_purpose",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "purpose_id")
    )
    private Set<Purpose> purposes = new HashSet<>();

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
