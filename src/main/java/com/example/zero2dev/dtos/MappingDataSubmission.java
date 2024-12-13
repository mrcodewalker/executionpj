package com.example.zero2dev.dtos;

import com.example.zero2dev.models.Contest;
import com.example.zero2dev.models.Language;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.models.User;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MappingDataSubmission {
    private User user;
    private Problem problem;
    private Language language;
    private Contest contest;
}
