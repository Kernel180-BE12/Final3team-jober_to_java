package com.example.final_projects.repository;

import com.example.final_projects.entity.EmailOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EmailOtpRepository extends JpaRepository<EmailOtp, Long>{

    @Query("select e from EmailOtp e where e.email = :email order by e.id desc")
    List<EmailOtp> findAllByEmailOrderByIdDesc(@Param("email") String email);

    @Modifying
    @Query("update EmailOtp e set e.attemptCount = e.attemptCount + 1 where e.id = :id")
    int incrementAttempt(@Param("id") Long id);

    //만료된 OTP 청소
    @Modifying
    @Query("delete from EmailOtp e where e.expiresAt < :now")
    int deleteExpired(@Param("now") LocalDateTime now);
}
