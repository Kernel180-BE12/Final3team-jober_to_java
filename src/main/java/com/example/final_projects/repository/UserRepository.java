package com.example.final_projects.repository;

import com.example.final_projects.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Modifying
    @Query("update User u set u.lastLoginAt = :ts where u.id = :id")
    int updateLastLoginAt(@Param("id") Long userId, @Param("ts") LocalDateTime timestamp);

    // (선택) 실패 카운트 +1
    @Modifying
    @Query("update User u set u.failCount = u.failCount + 1 where u.id = :id")
    int incrementFailCount(@Param("id") Long userId);

    // (선택) 실패 카운트 0으로
    @Modifying
    @Query("update User u set u.failCount = 0 where u.id = :id")
    int resetFailCount(@Param("id") Long userId);

    // (선택) 잠금/해제
    @Modifying
    @Query("update User u set u.locked = :locked, u.lockedAt = :lockedAt where u.id = :id")
    int setLocked(@Param("id") Long userId,
                  @Param("locked") boolean locked,
                  @Param("lockedAt") LocalDateTime lockedAt);

    // (선택) 상태 변경
    @Modifying
    @Query("update User u set u.status = :status where u.id = :id")
    int updateStatus(@Param("id") Long userId, @Param("status") User.Status status);
}
