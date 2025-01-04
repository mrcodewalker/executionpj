package com.example.zero2dev.repositories;

import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUser(User user);
    @Query("SELECT r FROM RefreshToken r WHERE r.user.id = :userId")
    Optional<RefreshToken> findByUserId(@Param("userId") Long userId);
}
