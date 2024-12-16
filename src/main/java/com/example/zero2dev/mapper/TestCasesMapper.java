package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.TestCasesDTO;
import com.example.zero2dev.models.TestCase;
import com.example.zero2dev.models.TestCases;
import com.example.zero2dev.responses.TestCasesResponse;
import com.example.zero2dev.utils.Base64Util;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", imports = {Base64Util.class})
public interface TestCasesMapper {
    TestCases toEntity(TestCasesDTO testCasesDTO);
    @Mapping(target = "input", expression = "java(Base64Util.decodeBase64(testCases.getInput()))")
    @Mapping(target = "output", expression = "java(Base64Util.decodeBase64(testCases.getOutput()))")
    TestCasesResponse toResponse(TestCases testCases);
}
