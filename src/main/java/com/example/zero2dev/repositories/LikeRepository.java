package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    @Query("SELECT l FROM Like l WHERE l.post.id = :postId AND l.user.id = :userId AND l.isActive = true")
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
}
