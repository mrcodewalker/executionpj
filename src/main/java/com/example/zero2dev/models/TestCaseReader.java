package com.example.zero2dev.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "test_case_reader")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TestCaseReader {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "problem_id", nullable = false)
    private Problem problem;

    @Column(name = "input_path", nullable = false)
    private String inputPath;

    @Column(name = "output_path", nullable = false)
    private String outputPath;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;
}
