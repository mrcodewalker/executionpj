package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.responses.ContestResponse;

import java.util.List;

public interface IContestService {
    ContestResponse createContest(ContestDTO contestDTO);
    ContestResponse updateContest(Long id, ContestDTO contestDTO);
    ContestResponse deleteContestById(Long id);
    List<ContestResponse> listContestValid();
    ContestResponse getContestById(Long id);
}
