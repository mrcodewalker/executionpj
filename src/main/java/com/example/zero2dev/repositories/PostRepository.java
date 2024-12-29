package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE p.isActive = true")
    Page<Post> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.user.id = :userId AND p.isActive = true")
    Page<Post> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId AND l.isActive = true")
    Long countLikesByPostId(Long postId);
    @Query("SELECT p FROM Post p WHERE p.isActive = true AND p.id = :postId")
    Post findByIdAndIsActive(@Param("postId") Long postId);
}
