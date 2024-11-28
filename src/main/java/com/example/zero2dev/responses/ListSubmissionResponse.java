package com.example.zero2dev.responses;

import com.example.zero2dev.dtos.SubmissionDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ListSubmissionResponse {
    private List<SubmissionResponse> submissionResponses;
}
