package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ContestParticipantDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.TimeNotValidException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.IContestParticipantService;
import com.example.zero2dev.models.*;
import com.example.zero2dev.repositories.*;
import com.example.zero2dev.responses.ContestParticipantResponse;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
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
    private final SubmissionRepository submissionRepository;
    @Override
    public ContestParticipantResponse joinContest(ContestParticipantDTO contestParticipantDTO) {
        SecurityService.validateUserIdExceptAdmin(contestParticipantDTO.getUserId());
        return Optional.of(contestParticipantDTO)
                .map(this::validateContestParticipation)
                .map(this::exchangeKey)
                .map(this::validateKey)
                .map(this.contestParticipantRepository::save)
                .map(this::exchangeResponse)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.DATA_SAVE_ERROR));
    }
    @Override
    public List<ContestParticipantResponse> getListUserJoined(Long contestId) {
        return Optional.ofNullable(this.contestParticipantRepository.findByIdContestId(contestId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(this::exchangeResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.DATA_RETRIEVAL_ERROR));
    }
    @Override
    public List<ContestParticipantResponse> getListContestUserJoined(Long userId) {
        SecurityService.validateUserIdExceptAdmin(userId);
        return Optional.ofNullable(this.contestParticipantRepository.findByIdUserId(userId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(this::exchangeResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public ContestParticipantResponse deleteByGroupKey(ContestParticipantKey contestParticipantKey) {
        return Optional.of(contestParticipantKey)
                .map(this::getRecordByKey)
                .map(this::deleteRecord)
                .map(this::exchangeResponse)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.DELETE_OBJECT_ERROR));
    }
    @Override
    public ContestParticipantResponse updateTotalScore(ContestParticipantKey contestParticipantKey, Long problemId) {
        SecurityService.validateUserIdExceptAdmin(contestParticipantKey.getUserId());
        ContestParticipant existingRecord = this.getRecordByKey(contestParticipantKey);
        Problem problem = this.getProblem(problemId);
        this.validateProblem(problem);
        return exchangeResponse(this.contestParticipantRepository.save(existingRecord));
    }

    @Override
    public boolean isUserJoinedContest(Long contestId, Long userId) {
        SecurityService.validateUserIdExceptAdmin(userId);
        return this.contestParticipantRepository.findById(this.mappingKey(contestId,userId)).isPresent();
    }

    @Override
    public ContestParticipantResponse getDetailByGroupKey(ContestParticipantKey contestParticipantKey) {
        return exchangeResponse(this.getRecordByKey(contestParticipantKey));
    }

    @Override
    public Long totalAcceptedByContest(Long contestId, Long userId) {
//        return (long)this.submissionRepository.selectAllAccepted(contestId, userId, SubmissionStatus.ACCEPTED).size();
        return 0L;
    }

    public ContestParticipant validateKey(ContestParticipantKey contestParticipantKey){
        Contest contest = this.contestRepository.findById(
                contestParticipantKey.getContestId()).orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        User user = this.userRepository.findById(
                contestParticipantKey.getUserId()).orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        return ContestParticipant.builder()
                .id(contestParticipantKey)
                .contest(contest)
                .user(user)
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
            throw new ValueNotValidException(MESSAGE.DATA_SAVE_ERROR);
        }
        return this.validateKey(this.exchangeKey(contestParticipantDTO));
    }
    private ContestParticipantResponse exchangeResponse(ContestParticipant contestParticipant){
        ContestParticipantResponse response = ContestParticipantResponse.builder()
                .contest(contestParticipant.getContest())
                .userId(contestParticipant.getUser().getId())
                .registeredTime(contestParticipant.getRegisteredTime())
                .build();

         Object[] data = this.submissionRepository.countAndSumByContestAndUser(contestParticipant.getContest().getId(),
                contestParticipant.getUser().getId(), SubmissionStatus.ACCEPTED).get(0);
//            System.out.println(data + "KEY");
            Long totalAccepted = (data[0] instanceof Long) ? (Long) data[0] : 0L;
            Long totalPoint = (data[1] instanceof Long) ? (Long) data[1] : 0L;
            response.setTotalAccepted(totalAccepted);
            response.setTotalPoint(totalPoint);
        return response;
    }
    private ContestParticipant getRecordByKey(ContestParticipantKey contestParticipantKey){
        this.validateKey(contestParticipantKey);
        if (!this.isUserJoinedContest(contestParticipantKey.getContestId(), contestParticipantKey.getUserId())){
            throw new ValueNotValidException(MESSAGE.INPUT_NOT_MATCH_EXCEPTION);
        }
        return this.contestParticipantRepository.findById(contestParticipantKey)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private Problem getProblem(Long problemId){
        return this.problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private void validateProblem(Problem problem){
        if (problem.getPoints()<0){
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }
    }
    private ContestParticipantDTO validateContestParticipation(ContestParticipantDTO dto) {
        if (isUserJoinedContest(dto.getContestId(), dto.getUserId())) {
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }
        return dto;
    }
    private ContestParticipant deleteRecord(ContestParticipant record) {
        contestParticipantRepository.deleteById(record.getId());
        return record;
    }
    public boolean joinedContest(Long contestId, Long userId){
        return this.contestParticipantRepository.existsByContest_IdAndUser_Id(contestId, userId);
    }
}
