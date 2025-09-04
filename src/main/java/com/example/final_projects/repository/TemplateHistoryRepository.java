package com.example.final_projects.repository;

import com.example.final_projects.entity.TemplateHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateHistoryRepository extends JpaRepository<TemplateHistory, Long> {
}
