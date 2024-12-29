package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.isActive = true")
    List<Comment> findByPostId(Long postId);
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId AND c.isActive = true")
    Long countByPostId(Long postId);
    Page<Comment> findByPostIdAndIsActiveTrue(Long postId, Pageable pageable);
}
