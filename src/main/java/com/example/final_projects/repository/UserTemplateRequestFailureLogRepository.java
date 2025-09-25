package com.example.final_projects.repository;

import com.example.final_projects.entity.UserTemplateRequestFailureLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTemplateRequestFailureLogRepository extends JpaRepository<UserTemplateRequestFailureLog, Long> {
}
