package com.example.zero2dev.services;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.TimeNotValidException;
import com.example.zero2dev.interfaces.IContestService;
import com.example.zero2dev.mapper.ContestMapper;
import com.example.zero2dev.models.Contest;
import com.example.zero2dev.repositories.ContestRepository;
import com.example.zero2dev.responses.ContestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
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
        List<Contest> contests = this.contestRepository.findAllNotExpired(LocalDateTime.now());
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
        return this.contestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find match contest with id: "+id));
    }
    public void validTime(ContestDTO contestDTO){
        if (contestDTO.getEndTime().isBefore(LocalDateTime.now())){
            throw new TimeNotValidException("Time was not found, please try again!");
        }
        if (contestDTO.getEndTime().isBefore(contestDTO.getStartTime())){
            throw new TimeNotValidException("End time must be greater than start time!");
        }
    }
}
