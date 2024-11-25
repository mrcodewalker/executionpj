package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.ContestDTO;
import com.example.zero2dev.models.Contest;
import com.example.zero2dev.responses.ContestResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ContestMapper {
    ContestResponse toResponse(Contest contest);
    Contest toEntity(ContestDTO contestDTO);
}
