package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.TimeNotValidException;
import com.example.zero2dev.interfaces.IContestService;
import com.example.zero2dev.mapper.ContestMapper;
import com.example.zero2dev.models.Contest;
import com.example.zero2dev.repositories.ContestRepository;
import com.example.zero2dev.responses.ContestResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContestService implements IContestService {
    private final ContestRepository contestRepository;
    private final ContestMapper mapper;
    @Override
    public ContestResponse createContest(ContestDTO contestDTO) {
        this.validTime(contestDTO);
        Contest contest = mapper.toEntity(contestDTO);
        return mapper.toResponse(this.contestRepository.save(contest));
    }
    @Override
    public ContestResponse updateContest(Long id, ContestDTO contestDTO) {
        Contest clone = this.findContestById(id);
        Contest contest = mapper.parseEntity(clone, contestDTO);
        return mapper.toResponse(this.contestRepository.save(contest));
    }

    @Override
    public ContestResponse deleteContestById(Long id) {
        Contest contest = this.findContestById(id);
        this.contestRepository.deleteById(id);
        return mapper.toResponse(contest);
    }

    @Override
    public List<ContestResponse> listContestValid() {
        List<Contest> contests = this.contestRepository.findValidContests(LocalDateTime.now());
        return contests.stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ContestResponse getContestById(Long id) {
        Contest contest = this.findContestById(id);
        return mapper.toResponse(contest);
    }
    public Contest findContestById(Long id){
        return this.contestRepository.findValidContestById(id, LocalDateTime.now())
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.CONTEST_EXPIRED_TIME));
    }
    public void validTime(ContestDTO contestDTO){
        if (contestDTO.getEndTime().isBefore(LocalDateTime.now())){
            throw new TimeNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        if (contestDTO.getEndTime().isBefore(contestDTO.getStartTime())){
            throw new TimeNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
    }
    public List<ContestResponse> filterAll(){
        return Optional.of(
                this.contestRepository.findAll())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(clone -> {
                            ContestResponse response = mapper.toResponse(clone);
                            response.setTag(clone.getTag());
                            response.setParticipants((long) clone.getParticipants().size());
                            return response;
                        })
                        .collect(Collectors.toList()))
                .orElse(new ArrayList<>());
    }
}
