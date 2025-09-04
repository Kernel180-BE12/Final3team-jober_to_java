package com.example.final_projects.repository;

import com.example.final_projects.entity.TemplateIndustry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateIndustryRepository extends JpaRepository<TemplateIndustry, Long> {
}
