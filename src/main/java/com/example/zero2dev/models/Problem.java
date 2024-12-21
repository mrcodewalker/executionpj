package com.example.zero2dev.models;

import com.example.zero2dev.storage.Difficulty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "problem")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Problem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficult;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "time_limit", columnDefinition = "BIGINT DEFAULT 1000")
    private Long timeLimit = 1000L;

    @Column(columnDefinition = "BIGINT DEFAULT 0")
    private Long points = 0L;
    @Column(columnDefinition = "DEFAULT NULL")
    private String tag;
    @Column(columnDefinition = "DEFAULT ''")
    private String constraints;
    @Column(columnDefinition = "DEFAULT ''")
    private String inputFormat;
    @Column(columnDefinition = "DEFAULT ''")
    private String outputFormat;
    @Column(name = "memory_limit", columnDefinition = "BIGINT DEFAULT 0")
    private Long memoryLimit;
    @Column(name = "limit_test", columnDefinition = "BIGINT DEFAULT 1")
    private Long limitTest;
    @Column(name = "contest_id", columnDefinition = "BIGINT DEFAULT 1")
    private Long contestId;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "problem")
    @JsonIgnore
    private List<TestCaseReader> testCases;

    @OneToMany(mappedBy = "problem")
    @JsonIgnore
    private List<Submission> submissions;
}
