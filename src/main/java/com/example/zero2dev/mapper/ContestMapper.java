package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.models.Contest;
import com.example.zero2dev.responses.ContestResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ContestMapper {
    ContestResponse toResponse(Contest contest);
    Contest toEntity(ContestDTO contestDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Contest parseEntity(@MappingTarget Contest contest,
                        ContestDTO contestDTO);
}
