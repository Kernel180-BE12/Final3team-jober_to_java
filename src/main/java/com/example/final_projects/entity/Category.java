package com.example.final_projects.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @Column(length = 10, nullable = false)
    private String id;

    @Column(length = 100)
    private String name;

    @Column(name = "parent_id", length = 10)
    private String parentId;

    @Column(columnDefinition = "json")
    private String keywords;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
