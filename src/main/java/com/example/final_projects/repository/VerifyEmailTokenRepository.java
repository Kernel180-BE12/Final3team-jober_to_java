package com.example.final_projects.repository;

import com.example.final_projects.entity.VerifyEmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerifyEmailTokenRepository extends JpaRepository<VerifyEmailToken,Long> {
    Optional<VerifyEmailToken> findByToken(String token);
}
