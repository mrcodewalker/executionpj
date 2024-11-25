package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.LanguageDTO;
import com.example.zero2dev.models.Language;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDTO toDTO(Language language);
    void updateLanguageFromDTO(LanguageDTO dto, @MappingTarget Language language);
}
