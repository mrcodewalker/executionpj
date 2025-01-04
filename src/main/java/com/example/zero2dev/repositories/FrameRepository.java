package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Frame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FrameRepository extends JpaRepository<Frame, Long> {
    Optional<Frame> findByIsDefaultTrue();
    List<Frame> findByPriceLessThanEqual(Long price);
    boolean existsByIsDefaultTrue();
}
