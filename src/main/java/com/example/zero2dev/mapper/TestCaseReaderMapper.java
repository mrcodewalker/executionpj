package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.TestCaseReaderDTO;
import com.example.zero2dev.models.TestCaseReader;
import com.example.zero2dev.responses.TestCaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestCaseReaderMapper {
    TestCaseReader toEntity(TestCaseReaderDTO testCaseReaderDTO);
    TestCaseResponse toResponse(TestCaseReader testCaseReader);
    List<TestCaseResponse> toResponseList(List<TestCaseReader> testCaseReaderList);
}
