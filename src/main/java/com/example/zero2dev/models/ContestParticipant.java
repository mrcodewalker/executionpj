package com.example.zero2dev.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contest_participant")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ContestParticipant {
    @EmbeddedId
    private ContestParticipantKey id;

    @ManyToOne
    @MapsId("contestId")
    @JoinColumn(name = "contest_id")
    @JsonBackReference
    private Contest contest;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "registered_time")
    @CreationTimestamp
    private LocalDateTime registeredTime;
}
