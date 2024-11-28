package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.CodeStorageDTO;
import com.example.zero2dev.models.CodeStorage;
import com.example.zero2dev.responses.CodeStorageResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CodeStorageMapper {
    CodeStorage toEntity(CodeStorageDTO codeStorageDTO);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    CodeStorage parseEntity(@MappingTarget CodeStorage codeStorage,
                                   CodeStorageDTO codeStorageDTO);
    CodeStorageResponse toResponse(CodeStorage codeStorage);
}
