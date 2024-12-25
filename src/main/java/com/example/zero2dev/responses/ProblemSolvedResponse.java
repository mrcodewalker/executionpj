package com.example.zero2dev.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProblemSolvedResponse {
    CustomPageResponse<ProblemResponse> page;
    @JsonProperty("response")
    private List<SolvedResponse> responseList;
}
