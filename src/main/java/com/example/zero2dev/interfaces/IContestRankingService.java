package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.ContestRankingDTO;
import com.example.zero2dev.models.ContestRanking;
import com.example.zero2dev.responses.ContestRankingResponse;

import java.util.List;

public interface IContestRankingService {
    ContestRankingResponse createContestRanking(ContestRankingDTO contestRankingDTO);
    ContestRankingResponse updateContestRanking(Long id, ContestRankingDTO contestRankingDTO);
    List<ContestRankingResponse> getUserRanking(Long userId);
    List<ContestRankingResponse> getListRankingByContestId(Long contestId);
    ContestRankingResponse getRankingUserContest(Long userId, Long contestId);
    ContestRankingResponse deleteUserRankingById(Long id);
    ContestRankingResponse getById(Long id);
    List<List<ContestRankingResponse>> getListHighestScoreByEachContest();
}
