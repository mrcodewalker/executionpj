package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.ContestParticipantDTO;
import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.models.ContestParticipant;
import com.example.zero2dev.models.ContestParticipantKey;
import com.example.zero2dev.responses.ContestParticipantResponse;

import java.util.List;

public interface IContestParticipantService {
    ContestParticipantResponse joinContest(ContestParticipantDTO contestParticipantDTO);
    List<ContestParticipantResponse> getListUserJoined(Long contestId);
    List<ContestParticipantResponse> getListContestUserJoined(Long userId);
    ContestParticipantResponse deleteByGroupKey(ContestParticipantKey contestParticipantKey);
    ContestParticipantResponse updateTotalScore(ContestParticipantKey contestParticipantKey, Long problemId);
    boolean isUserJoinedContest(Long contestId, Long userId);
    ContestParticipantResponse getDetailByGroupKey(ContestParticipantKey contestParticipantKey);
    Long totalAcceptedByContest(Long contestId, Long userId);
}
