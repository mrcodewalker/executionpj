package com.example.zero2dev.repositories;

import com.example.zero2dev.models.ContestParticipant;
import com.example.zero2dev.models.ContestParticipantKey;
import com.example.zero2dev.services.ContestParticipantService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, ContestParticipantKey> {
    List<ContestParticipant> findByIdContestId(Long contestId);

    List<ContestParticipant> findByIdUserId(Long userId);
    Optional<ContestParticipant> findByContest_IdAndUser_Id(Long contestId, Long userId);
    boolean existsByContest_IdAndUser_Id(Long contestId, Long userId);
}
