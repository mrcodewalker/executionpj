package com.example.zero2dev.models;

import lombok.Data;

@Data
public class Certificate {
    private String studentName;
    private String contestName;
    private Integer completedTasks;
    private String ranking;
    private String signatureName1;
    private String signatureName2;
    private String signatureDate1;
    private String signatureDate2;
    private String signer1Reason;
    private String signer2Reason;
}