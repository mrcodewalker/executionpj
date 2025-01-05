package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Frame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrameRepository extends JpaRepository<Frame, Long> {
    Optional<Frame> findByIsDefaultTrue();
    List<Frame> findByPriceLessThanEqual(Long price);
    boolean existsByIsDefaultTrue();
    @Query(value = """
        SELECT 
            f.id AS id, 
            f.name AS name, 
            f.description AS description, 
            f.image_url AS imageUrl, 
            f.frame_type AS frameType, 
            f.css_animation AS cssAnimation, 
            f.price AS price, 
            f.is_default AS isDefault,
            u.gems AS gems,
            CASE 
                WHEN us.user_id IS NOT NULL THEN '1' 
                ELSE '0' 
            END AS status,
            CASE 
                WHEN us.is_active = 1 THEN '1' 
                ELSE '0' 
            END AS isCurrent
        FROM frame f
        JOIN user u ON u.id = :userId
        LEFT JOIN user_frame us 
        ON us.frame_id = f.id AND us.user_id = :userId
        """, nativeQuery = true)
    List<Object[]> findAllFramesWithCurrentStatus(@Param("userId") Long userId);
}
