package com.example.final_projects.repository;

import com.example.final_projects.entity.Template;
import com.example.final_projects.entity.TemplateStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TemplateRepository extends JpaRepository<Template, Long> {
    Page<Template> findByUserIdAndStatus(Long userId, TemplateStatus status, Pageable pageable);
}
