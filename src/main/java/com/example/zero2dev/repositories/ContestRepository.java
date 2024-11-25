package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Contest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContestRepository extends JpaRepository<Contest, Long> {
    @Query("SELECT c FROM Contest c WHERE c.endTime > :now")
    List<Contest> findAllNotExpired(@Param("now") LocalDateTime now);
}
