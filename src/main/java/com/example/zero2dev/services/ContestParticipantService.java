package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ContestParticipantDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.TimeNotValidException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.IContestParticipantService;
import com.example.zero2dev.models.*;
import com.example.zero2dev.repositories.ContestParticipantRepository;
import com.example.zero2dev.repositories.ContestRepository;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.responses.ContestParticipantResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestParticipantService implements IContestParticipantService {
    private final ContestParticipantRepository contestParticipantRepository;
    private final ContestRepository contestRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Override
    public ContestParticipantResponse joinContest(ContestParticipantDTO contestParticipantDTO) {
        return Optional.of(contestParticipantDTO)
                .map(this::validateContestParticipation)
                .map(this::exchangeKey)
                .map(this::validateKey)
                .map(this.contestParticipantRepository::save)
                .map(this::exchangeResponse)
                .orElseThrow(() -> new ValueNotValidException("Cannot process contest participation"));
    }

    @Override
    public List<ContestParticipantResponse> getListUserJoined(Long contestId) {
        return Optional.ofNullable(this.contestParticipantRepository.findByIdContestId(contestId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(this::exchangeResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find with contest id right now"));
    }
    @Override
    public List<ContestParticipantResponse> getListContestUserJoined(Long userId) {
        return Optional.ofNullable(this.contestParticipantRepository.findByIdUserId(userId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(this::exchangeResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find with user id right now"));
    }

    @Override
    public ContestParticipantResponse deleteByGroupKey(ContestParticipantKey contestParticipantKey) {
        ContestParticipant contestParticipant = this.getRecordByKey(contestParticipantKey);
        this.contestParticipantRepository.deleteById(contestParticipantKey);
        return exchangeResponse(contestParticipant);
    }
    @Override
    public ContestParticipantResponse updateTotalScore(ContestParticipantKey contestParticipantKey, Long problemId) {
        ContestParticipant existingRecord = this.getRecordByKey(contestParticipantKey);
        Problem problem = this.getProblem(problemId);
        this.validateProblem(problem);
        existingRecord.setTotalScore(existingRecord.getTotalScore()+problem.getPoints());
        return exchangeResponse(this.contestParticipantRepository.save(
                existingRecord));
    }

    @Override
    public boolean isUserJoinedContest(Long contestId, Long userId) {
        return this.contestParticipantRepository.findById(this.mappingKey(contestId,userId)).isPresent();
    }

    @Override
    public ContestParticipantResponse getDetailByGroupKey(ContestParticipantKey contestParticipantKey) {
        return exchangeResponse(this.getRecordByKey(contestParticipantKey));
    }
    public ContestParticipant validateKey(ContestParticipantKey contestParticipantKey){
        Contest contest = this.contestRepository.findById(
                contestParticipantKey.getContestId()).orElseThrow(() -> new ResourceNotFoundException("Can not find contest"));
        User user = this.userRepository.findById(
                contestParticipantKey.getUserId()).orElseThrow(() -> new ResourceNotFoundException("Can not find user"));
        return ContestParticipant.builder()
                .id(contestParticipantKey)
                .contest(contest)
                .user(user)
                .totalScore(0L)
                .build();
    }
    public ContestParticipantKey exchangeKey(ContestParticipantDTO contestParticipantDTO){
        return new ContestParticipantKey(contestParticipantDTO.getContestId(), contestParticipantDTO.getUserId());
    }
    public ContestParticipantKey mappingKey(Long contestId, Long userId){
        return new ContestParticipantKey(contestId, userId);
    }
    public ContestParticipant checkValid(ContestParticipantDTO contestParticipantDTO){
        if(this.isUserJoinedContest(contestParticipantDTO.getContestId(), contestParticipantDTO.getUserId())){
            throw new ValueNotValidException("Can not join contest right now");
        }
        return this.validateKey(this.exchangeKey(contestParticipantDTO));
    }
    private ContestParticipantResponse exchangeResponse(ContestParticipant contestParticipant){
        return ContestParticipantResponse.builder()
                .contest(contestParticipant.getContest())
                .userId(contestParticipant.getUser().getId())
                .registeredTime(contestParticipant.getRegisteredTime())
                .totalScore(contestParticipant.getTotalScore())
                .build();
    }
    private ContestParticipant getRecordByKey(ContestParticipantKey contestParticipantKey){
        this.validateKey(contestParticipantKey);
        if (!this.isUserJoinedContest(contestParticipantKey.getContestId(), contestParticipantKey.getUserId())){
            throw new ValueNotValidException("Value not valid exception");
        }
        return this.contestParticipantRepository.findById(contestParticipantKey)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find record"));
    }
    private Problem getProblem(Long problemId){
        return this.problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find value"));
    }
    private void validateProblem(Problem problem){
        if (problem.getPoints()<0){
            throw new ValueNotValidException("Can not submit right now");
        }
    }
    private ContestParticipantDTO validateContestParticipation(ContestParticipantDTO dto) {
        if (isUserJoinedContest(dto.getContestId(), dto.getUserId())) {
            throw new ValueNotValidException("Cannot join contest right now");
        }
        return dto;
    }
}
