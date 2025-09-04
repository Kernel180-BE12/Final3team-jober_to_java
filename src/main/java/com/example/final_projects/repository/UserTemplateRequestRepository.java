package com.example.final_projects.repository;

import com.example.final_projects.entity.UserTemplateRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserTemplateRequestRepository extends JpaRepository<UserTemplateRequest, Long> {
}
