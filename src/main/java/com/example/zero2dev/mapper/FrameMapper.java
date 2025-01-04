package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.CreateFrameRequest;
import com.example.zero2dev.dtos.FrameDTO;
import com.example.zero2dev.dtos.UpdateFrameRequest;
import com.example.zero2dev.dtos.UserFrameDTO;
import com.example.zero2dev.models.Frame;
import com.example.zero2dev.models.UserFrame;
import org.hibernate.query.sqm.FrameExclusion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FrameMapper {
    Frame toEntity(CreateFrameRequest createFrameRequest);
    Frame toEntity(UpdateFrameRequest updateFrameRequest);
    @Mapping(target = "frameId", source = "id")
    UserFrameDTO toDTO(UserFrame frame);
    @Mapping(target = "frameId", source = "id")
    FrameDTO toDTO(Frame frame);
    @Mapping(target = "cssAnimation", source = "cssAnimation")
    void updateFrameFromDTO(CreateFrameRequest dto, @MappingTarget Frame frame);
}
