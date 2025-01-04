package com.example.zero2dev.responses;

import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateResponse {
    private Long languageCount;
    private String languageName;
    private String languageVersion;
    private String fullName;
    private Long totalSolved;
    private boolean completedAllProblems;
    private String contestTitle;
    private Long rankInContest;
    private Long total;
}
