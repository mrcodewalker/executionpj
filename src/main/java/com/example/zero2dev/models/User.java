package com.example.zero2dev.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "user")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100, name = "username")
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 20)
    private String phoneNumber;
    @Column(name = "full_name")
    private String fullName;
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "total_solved", columnDefinition = "BIGINT DEFAULT 0")
    private Long totalSolved = 0L;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "is_active", columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user")
    private List<Submission> submissions;

    @OneToMany(mappedBy = "user")
    private List<ContestParticipant> contestParticipants;

    @OneToMany(mappedBy = "user")
    private List<ContestRanking> contestRankings;
}
