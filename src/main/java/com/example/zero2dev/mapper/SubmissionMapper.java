package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.SubmissionDTO;
import com.example.zero2dev.models.Submission;
import com.example.zero2dev.responses.SubmissionResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SubmissionMapper {
//    SubmissionResponse toResponse(Submission submission);
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "executionTime", ignore = true)
    @Mapping(target = "memoryUsed", ignore = true)
    Submission toEntity(SubmissionDTO submissionDTO);
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    Submission parseEntity(@MappingTarget Submission submission,
//                           SubmissionDTO submissionDTO);
}
