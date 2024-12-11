package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.UserDTO;
import com.example.zero2dev.models.User;
import com.example.zero2dev.responses.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @Mapping(target = "role", ignore = true)
    UserResponse toResponse(User user);
    User toEntity(UserDTO userDTO);
    UserDTO toDTO(User user);
}
