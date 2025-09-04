package com.example.final_projects.repository;

import com.example.final_projects.entity.TemplateButton;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateButtonRepository extends JpaRepository<TemplateButton, Long> {
}
