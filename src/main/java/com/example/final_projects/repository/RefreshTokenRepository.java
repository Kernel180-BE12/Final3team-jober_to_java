package com.example.final_projects.repository;

import com.example.final_projects.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    Optional<RefreshToken> findByJti(String Jti);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken r
           SET r.replacedByJti = :newJti,
               r.revoked = true,
               r.rotatedAt = CURRENT_TIMESTAMP
         WHERE r.jti = :oldJti
           AND r.revoked = false
           AND r.replacedByJti IS NULL
""")
    int markRotated(@Param("oldJti") String oldJti, @Param("newJti") String newJti);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken r
           SET r.revoked = true
         WHERE r.id = :id  
""")
    int revokedById(@Param("id")Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        UPDATE RefreshToken r
           SET r.revoked = true
         WHERE r.userId = :userId
           AND r.expiresAt > :now  
""")
    int revokedAllByUserId(@Param("userId") Long userId, @Param("now") LocalDateTime now);
}
