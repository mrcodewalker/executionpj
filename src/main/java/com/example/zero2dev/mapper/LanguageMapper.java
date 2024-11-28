package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.LanguageDTO;
import com.example.zero2dev.models.Language;
import com.example.zero2dev.responses.LanguageResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LanguageMapper {
    LanguageDTO toDTO(Language language);
    Language toEntity(LanguageDTO languageDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Language parseEntity(@MappingTarget Language language, LanguageDTO languageDTO);
    void updateLanguageFromDTO(LanguageDTO dto, @MappingTarget Language language);
    LanguageResponse toResponse(Language language);
}
