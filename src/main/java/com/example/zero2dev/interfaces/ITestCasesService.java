package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.TestCasesDTO;
import com.example.zero2dev.models.TestCase;
import com.example.zero2dev.models.TestCases;
import com.example.zero2dev.responses.ListTestCaseResponse;
import com.example.zero2dev.responses.TestCaseResponse;
import com.example.zero2dev.responses.TestCasesResponse;

import java.util.List;

public interface ITestCasesService {
    ListTestCaseResponse createTestCase(TestCasesDTO testCasesDTO);
    ListTestCaseResponse updateTestCase(Long testCaseId, TestCasesDTO testCasesDTO);
    ListTestCaseResponse deleteByTestCaseId(Long testCaseId);
    ListTestCaseResponse deleteAllTestCase(Long problemId);
    ListTestCaseResponse getListTestCaseByProblemId(Long problemId);
    ListTestCaseResponse getTestCaseByProblemIdAndOrder(Long problemId, Long orderId);
    ListTestCaseResponse getAllTestCase(Long problemId);
    TestCase mappingEntityToValidTestCase(TestCases testCases);
    TestCase mappingResponseToValidTestCase(TestCasesResponse testCasesResponse);
}
