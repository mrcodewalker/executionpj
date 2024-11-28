package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Contest;
import com.example.zero2dev.models.ContestRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public interface ContestRankingRepository extends JpaRepository<ContestRanking, Long> {
    List<ContestRanking> findByUser_Id(Long userId);
    List<ContestRanking> findByContest_Id(Long contestId);
    @Query("SELECT c FROM ContestRanking c " +
            "WHERE c.user.id = :userId AND c.contest.id = :contestId")
    ContestRanking findByUserIdAndContestId(@Param("userId") Long userId,
                                            @Param("contestId") Long contestId);
    @Query("SELECT cr FROM ContestRanking cr ORDER BY cr.contest.id, cr.score DESC")
    List<ContestRanking> findAllRankingsOrderedByContestAndScore();
    default Map<Long, List<ContestRanking>> getContestRankingGroupedByContest() {
        return findAllRankingsOrderedByContestAndScore().stream()
                .collect(Collectors.groupingBy(
                        cr -> cr.getContest().getId(),
                        Collectors.toList()
                ));
    }
}
