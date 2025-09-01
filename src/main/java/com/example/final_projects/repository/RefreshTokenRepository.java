package com.example.final_projects.repository;

import com.example.final_projects.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    @Modifying @Transactional
    @Query("update RefreshToken r set r.revoked=true where r.user.id=:userId and r.revoked=false")
    int revokeAllActiveByUserId(@Param("userId") Long userId);
}
