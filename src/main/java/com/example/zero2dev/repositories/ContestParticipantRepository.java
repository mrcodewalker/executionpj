package com.example.zero2dev.repositories;

import com.example.zero2dev.models.ContestParticipant;
import com.example.zero2dev.models.ContestParticipantKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContestParticipantRepository extends JpaRepository<ContestParticipant, ContestParticipantKey> {
    List<ContestParticipant> findByIdContestId(Long contestId);

    List<ContestParticipant> findByIdUserId(Long userId);
}
