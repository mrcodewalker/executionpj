package com.example.zero2dev.mapper;

import com.example.zero2dev.dtos.TestCaseReaderDTO;
import com.example.zero2dev.models.TestCaseReader;
import com.example.zero2dev.responses.TestCaseResponse;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TestCaseReaderMapper {
    TestCaseReader toEntity(TestCaseReaderDTO testCaseReaderDTO);
    TestCaseResponse toResponse(TestCaseReader testCaseReader);
    List<TestCaseResponse> toResponseList(List<TestCaseReader> testCaseReaderList);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    TestCaseReader parseEntity(@MappingTarget TestCaseReader testCaseReader,
                               TestCaseReaderDTO testCaseReaderDTO);
}
