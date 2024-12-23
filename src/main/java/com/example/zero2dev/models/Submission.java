    package com.example.zero2dev.models;

    import com.example.zero2dev.storage.SubmissionStatus;
    import com.fasterxml.jackson.annotation.JsonBackReference;
    import jakarta.persistence.*;
    import lombok.*;
    import org.hibernate.annotations.CreationTimestamp;

    import java.time.LocalDateTime;

    @Entity
    @Table(name = "submission")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public class Submission {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        @JsonBackReference
        private User user;

        @ManyToOne
        @JoinColumn(name = "problem_id", nullable = false)
        private Problem problem;
        @ManyToOne
        @JoinColumn(name = "contest_id", nullable = false)
        @JsonBackReference
        private Contest contest;
        @ManyToOne
        @JoinColumn(name = "language_id", nullable = false)
        private Language language;

        @Enumerated(EnumType.STRING)
        @Column(name = "status", nullable = false)
        private SubmissionStatus status;

        @Column(name = "execution_time")
        private Long executionTime;

        @Column(name = "memory_used")
        private Long memoryUsed;

        @Column(name = "created_at")
        @CreationTimestamp
        private LocalDateTime createdAt;
        @Column(name = "message", columnDefinition = "VARCHAR(255) DEFAULT ''")
        private String message;
        @Column(name = "failed_at", columnDefinition = "BIGINT DEFAULT 0")
        private Long failedAt;
        @Column(name = "total_test", columnDefinition = "BIGINT DEFAULT 0")
        private Long totalTest;
    }
