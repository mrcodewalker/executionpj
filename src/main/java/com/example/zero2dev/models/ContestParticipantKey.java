package com.example.zero2dev.models;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class ContestParticipantKey implements Serializable {
    private Long contestId;
    private Long userId;
}