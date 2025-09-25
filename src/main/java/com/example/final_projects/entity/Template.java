package com.example.final_projects.entity;

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

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
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

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "template_industry",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "industry_id")
    )
    @Builder.Default
    private Set<Industry> industries = new HashSet<>();

    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "template_purpose",
            joinColumns = @JoinColumn(name = "template_id"),
            inverseJoinColumns = @JoinColumn(name = "purpose_id")
    )
    @Builder.Default
    private Set<Purpose> purposes = new HashSet<>();

    public void addButtons(List<TemplateButton> buttons) {
        this.buttons.clear();
        if (buttons != null) {
            this.buttons.addAll(buttons);
            buttons.forEach(button -> button.setTemplate(this)); // 양방향 관계 설정
        }
    }

    public void addVariables(List<TemplateVariable> variables) {
        this.variables.clear();
        if (variables != null) {
            this.variables.addAll(variables);
            variables.forEach(variable -> variable.setTemplate(this)); // 양방향 관계 설정
        }
    }
}
