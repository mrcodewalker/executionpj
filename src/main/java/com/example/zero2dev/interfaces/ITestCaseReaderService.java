package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.TestCaseReaderDTO;
import com.example.zero2dev.models.TestCaseReader;
import com.example.zero2dev.responses.ListTestCaseReaderResponse;
import com.example.zero2dev.responses.TestCaseReaderResponse;

import java.util.List;

public interface ITestCaseReaderService {
    TestCaseReaderResponse createTestCase(TestCaseReaderDTO testCaseReaderDTO);
    TestCaseReaderResponse updateTestCase(Long id, TestCaseReaderDTO testCaseReaderDTO);
    TestCaseReaderResponse deleteTestCase(Long id);
    ListTestCaseReaderResponse getListTestCase(Long problemId);
    TestCaseReaderResponse getTestCaseById(Long id);
}
