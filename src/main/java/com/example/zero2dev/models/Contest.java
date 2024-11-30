package com.example.zero2dev.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contest")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(length = 50)
    private String type;

    @OneToMany(mappedBy = "contest")
    @JsonIgnore
    private List<ContestParticipant> participants;

    @OneToMany(mappedBy = "contest")
    @JsonManagedReference
    private List<ContestRanking> rankings;
}