package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    @Query("SELECT c FROM Contest c WHERE c.endTime > :now")
    List<Contest> findAllNotExpired(@Param("now") LocalDateTime now);
    @Query("SELECT c FROM Contest c WHERE c.endTime > :now AND c.startTime <= :now")
    List<Contest> findValidContests(LocalDateTime now);
    @Query("SELECT c FROM Contest c WHERE c.id = :id AND c.endTime > :now AND c.startTime <= :now")
    Optional<Contest> findValidContestById(Long id, LocalDateTime now);
}
