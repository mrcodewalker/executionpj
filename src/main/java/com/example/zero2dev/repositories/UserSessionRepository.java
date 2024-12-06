package com.example.zero2dev.repositories;

import com.example.zero2dev.models.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, Long> {
    Optional<UserSession> findBySessionId(String sessionId);

    List<UserSession> findByUserId(Long userId);

    List<UserSession> findByIsActiveTrue();

    @Query("SELECT us FROM UserSession us WHERE us.userId = :userId AND us.isActive = true")
    List<UserSession> findActiveSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT us FROM UserSession us WHERE us.expiredAt < :currentTime")
    List<UserSession> findExpiredSessions(@Param("currentTime") LocalDateTime currentTime);

    void deleteBySessionId(String sessionId);

    long countByUserId(Long userId);
    Optional<UserSession> findTopByUserIdAndIsActiveAndExpiredAtAfterOrderByCreatedAtDesc(
            Long userId, boolean isActive, LocalDateTime currentTime);
}
