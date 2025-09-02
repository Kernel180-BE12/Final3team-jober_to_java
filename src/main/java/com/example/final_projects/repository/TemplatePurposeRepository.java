package com.example.final_projects.repository;

import com.example.final_projects.entity.TemplatePurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplatePurposeRepository extends JpaRepository<TemplatePurpose, Long> {
}
