package com.example.zero2dev.dtos;

import com.example.zero2dev.models.Category;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.storage.Difficulty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ProblemDTO {
    private String title;
    private String description;
    private Difficulty difficult;
    private Long categoryId;
    private Long timeLimit;
    private Long points;
    private Boolean isActive;
    private String tag;
    private Long memoryLimit;
    private Long limitTest;
    private String inputFormat;
    private String outputFormat;
    public static ProblemDTO fromEntity(Problem problem) {
        ProblemDTO dto = new ProblemDTO();
        dto.setTitle(problem.getTitle());
        dto.setDescription(problem.getDescription());
        dto.setDifficult(problem.getDifficult());
        dto.setCategoryId(problem.getCategory().getId());
        dto.setTimeLimit(problem.getTimeLimit());
        dto.setPoints(problem.getPoints());
        dto.setIsActive(problem.getIsActive());
        return dto;
    }
    public static ProblemResponse exchangeEntity(Problem problem){
        return ProblemResponse.builder()
                .description(problem.getDescription())
                .category(problem.getCategory())
                .difficult(problem.getDifficult())
                .points(problem.getPoints())
                .timeLimit(problem.getTimeLimit())
                .title(problem.getTitle())
                .tag(problem.getTag())
                .build();
    }
    public static Problem createFromEntity(Category category, ProblemDTO problemDTO){
        return Problem.builder()
                .title(problemDTO.getTitle())
                .description(problemDTO.getDescription())
                .difficult(problemDTO.getDifficult())
                .category(category)
                .timeLimit(problemDTO.getTimeLimit())
                .points(problemDTO.getPoints())
                .isActive(true)
                .tag(problemDTO.getTag())
                .build();
    }
}