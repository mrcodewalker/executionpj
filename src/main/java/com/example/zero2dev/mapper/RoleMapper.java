package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.RoleDTO;
import com.example.zero2dev.dtos.TestCaseReaderDTO;
import com.example.zero2dev.models.Role;
import com.example.zero2dev.models.TestCaseReader;
import com.example.zero2dev.responses.RoleResponse;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    Role toEntity(RoleDTO roleDTO);
    RoleResponse toResponse(Role role);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Role parseEntity(@MappingTarget Role role,
                               RoleDTO roleDTO);
}
