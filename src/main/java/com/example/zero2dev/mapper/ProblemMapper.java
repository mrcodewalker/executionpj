package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.ProblemDTO;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.responses.ProblemResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProblemMapper {
    @Mapping(source = "category.id", target = "categoryId")
    ProblemDTO toDTO(Problem problem);
    ProblemResponse toResponse(Problem problem);
    void updateProblemFromDto(ProblemDTO dto, @MappingTarget Problem problem);
}