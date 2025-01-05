package com.example.zero2dev.repositories;

import com.example.zero2dev.models.UserFrame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFrameRepository extends JpaRepository<UserFrame, Long> {
    List<UserFrame> findByUserId(Long userId);
    Optional<UserFrame> findByUserIdAndFrameId(Long userId, Long frameId);
    Optional<UserFrame> findByUserIdAndIsActiveTrue(Long userId);
    @Modifying
    @Query("UPDATE UserFrame uf SET uf.isActive = false WHERE uf.userId = :userId")
    void setAllFramesInactiveForUser(@Param("userId") Long userId);
    @Query(value = "SELECT f.id, f.name, f.description, f.image_url, f.frame_type, f.css_animation, f.price, f.is_default " +
            "FROM user_frame uf " +
            "JOIN user u ON u.id = uf.user_id " +
            "JOIN frame f ON f.id = uf.frame_id " +
            "WHERE u.id = :userId AND uf.is_active = :isActive", nativeQuery = true)
    List<Object[]> findFrameDetailsByUsername(@Param("userId") Long userId, @Param("isActive") boolean isActive);
}
