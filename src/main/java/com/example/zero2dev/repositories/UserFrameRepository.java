package com.example.zero2dev.repositories;

import com.example.zero2dev.models.UserFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFrameRepository extends JpaRepository<UserFrame, Long> {
    List<UserFrame> findByUserId(Long userId);
    Optional<UserFrame> findByUserIdAndFrameId(Long userId, Long frameId);
    Optional<UserFrame> findByUserIdAndIsActiveTrue(Long userId);
}
