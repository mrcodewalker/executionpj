package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.ContestRankingDTO;
import com.example.zero2dev.models.ContestRanking;
import com.example.zero2dev.responses.ContestRankingResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ContestRankingMapper {
    ContestRankingResponse toResponse(ContestRanking contestRanking);
    ContestRanking toDTO(ContestRankingDTO contestRankingDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    ContestRanking parseEntity(@MappingTarget ContestRanking contestRanking,
                               ContestRankingDTO contestRankingDTO);
}
