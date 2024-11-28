package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ContestRankingDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.IContestRankingService;
import com.example.zero2dev.mapper.ContestMapper;
import com.example.zero2dev.mapper.ContestRankingMapper;
import com.example.zero2dev.models.Contest;
import com.example.zero2dev.models.ContestRanking;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.ContestRankingRepository;
import com.example.zero2dev.repositories.ContestRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.responses.ContestRankingResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestRankingService implements IContestRankingService {
    private final ContestRankingRepository contestRankingRepository;
    private final ContestRankingMapper mapper;
    private final UserRepository userRepository;
    private final ContestRepository contestRepository;
    @Override
    public ContestRankingResponse createContestRanking(ContestRankingDTO contestRankingDTO) {
        return ContestRankingResponse.exchangeEntity(
                this.contestRankingRepository.save(this.toEntity(contestRankingDTO)));
    }

    @Override
    public ContestRankingResponse updateContestRanking(Long id, ContestRankingDTO contestRankingDTO) {
        return ContestRankingResponse.exchangeEntity(this.contestRankingRepository.save(
                this.anotherMapper(id, contestRankingDTO)));
    }

    @Override
    public List<ContestRankingResponse> getUserRanking(Long userId) {
        return Optional.ofNullable(this.contestRankingRepository.findByUser_Id(userId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(ContestRankingResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not get list by user ranking right now"));
    }

    @Override
    public List<ContestRankingResponse> getListRankingByContestId(Long contestId) {
        return Optional.ofNullable(this.contestRankingRepository.findByContest_Id(contestId))
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(ContestRankingResponse::exchangeEntity)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not get list by contest id right now"));
    }

    @Override
    public ContestRankingResponse getRankingUserContest(Long userId, Long contestId) {
        return Optional.ofNullable(this.contestRankingRepository.findByUserIdAndContestId(userId, contestId))
                .map(ContestRankingResponse::exchangeEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Can not fill data right now"));
    }

    @Override
    public ContestRankingResponse deleteUserRankingById(Long id) {
        ContestRanking contestRanking = this.getByContestRankingId(id);
        this.contestRankingRepository.deleteById(id);
        return ContestRankingResponse.exchangeEntity(contestRanking);
    }

    @Override
    public ContestRankingResponse getById(Long id) {
        return ContestRankingResponse.exchangeEntity(this.contestRankingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find contest ranking right now")));
    }

    @Override
    public List<List<ContestRankingResponse>> getListHighestScoreByEachContest() {
        Map<Long, List<ContestRanking>> groupedRankings = this.contestRankingRepository.getContestRankingGroupedByContest();

        return groupedRankings.values().stream()
                .map(rankings -> rankings.stream()
                        .map(ContestRankingResponse::exchangeEntity)
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }
    public ContestRanking getByContestRankingId(Long id){
        return this.contestRankingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find contest ranking right now"));
    }
    public User getUser(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find user"));
    }
    public Contest getContest(Long id){
        return this.contestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find contest"));
    }
    public ContestRanking toEntity(ContestRankingDTO contestRankingDTO){
        User user = this.getUser(contestRankingDTO.getUserId());
        Contest contest = this.getContest(contestRankingDTO.getContestId());
        return ContestRanking.builder()
                .rank(contestRankingDTO.getRank())
                .score(contestRankingDTO.getScore())
                .user(user)
                .contest(contest)
                .build();
    }
    public ContestRanking findExistRecord(Long id){
        return this.contestRankingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find contest ranking with id"));
    }
    public ContestRanking anotherMapper(Long id, ContestRankingDTO contestRankingDTO){
        ContestRanking existContestRanking = this.findExistRecord(id);
        ContestRanking ranking = mapper.parseEntity(existContestRanking, contestRankingDTO);
        ranking.setUser(this.getUser(contestRankingDTO.getUserId()));
        ranking.setContest(this.getContest(contestRankingDTO.getContestId()));
        return ranking;
    }
}
