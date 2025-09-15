package com.example.final_projects.repository;

import com.example.final_projects.entity.VerifyEmailToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerifyEmailTokenRepository extends JpaRepository<VerifyEmailToken,Long> {
    Optional<VerifyEmailToken> findByToken(String token);

    //가입 시 토큰 1회성 소진
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update VerifyEmailToken t
           set t.used =true
         where t.token = :token and t.used = false                
        """)
    int markUsedByToken(@Param("token") String token);

    //만료/사용된 pre-sign 토큰 캇
    @Modifying
    @Query("""
        delete from VerifyEmailToken t
        where t.preSignup = true
        and(t.used = true or t.expiresAt < :now)
    """)
    int deleteOldPreSignupTokens(@Param("now") LocalDateTime now);
}
