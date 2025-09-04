package com.example.final_projects.repository;

import com.example.final_projects.entity.TemplateVariable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateVariableRepository extends JpaRepository<TemplateVariable, Long> {
}
