package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.models.Contest;
import com.example.zero2dev.models.ContestParticipant;
import com.example.zero2dev.responses.ContestResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ContestMapper {
    @Mapping(target = "participants", expression = "java(mapParticipants(contest.getParticipants()))")
    ContestResponse toResponse(Contest contest);
    Contest toEntity(ContestDTO contestDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Contest parseEntity(@MappingTarget Contest contest,
                        ContestDTO contestDTO);
    default Long mapParticipants(List<ContestParticipant> participants) {
        return participants != null ? (long) participants.size() : null;
    }
}
