package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.TestCasesDTO;
import com.example.zero2dev.models.TestCase;
import com.example.zero2dev.models.TestCases;
import com.example.zero2dev.responses.TestCasesResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TestCasesMapper {
    TestCases toEntity(TestCasesDTO testCasesDTO);
    TestCasesResponse toResponse(TestCases testCases);
}
