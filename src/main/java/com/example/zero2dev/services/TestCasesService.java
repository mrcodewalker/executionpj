package com.example.zero2dev.services;

import com.example.zero2dev.dtos.TestCasesDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ITestCasesService;
import com.example.zero2dev.mapper.TestCasesMapper;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.models.TestCase;
import com.example.zero2dev.models.TestCases;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.TestCaseRepository;
import com.example.zero2dev.responses.*;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.utils.Base64Util;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestCasesService implements ITestCasesService {
    private final ProblemRepository problemRepository;
    private final TestCaseRepository testCaseRepository;
    private final TestCasesMapper mapper;
    @Override
    public ListTestCaseResponse createTestCase(TestCasesDTO testCasesDTO) {
        TestCases testCases = new TestCases();
        Problem problem = this.getProblem(testCasesDTO.getProblemId());
        testCases.setProblem(problem);
        testCases.setIsActive(true);
        long count = this.testCaseRepository.
                countByProblemIdAndIsActive(testCases.getProblem().getId(), true);
        testCases.setTestCaseOrder(count+1);
        testCases.setInput(Base64Util.encodeBase64(testCasesDTO.getInput()));
        testCases.setOutput(Base64Util.encodeBase64(testCasesDTO.getOutput()));
        List<TestCasesResponse> list = new ArrayList<>();
        ListTestCaseResponse listTestCaseResponse = new ListTestCaseResponse();
        TestCasesResponse response = mapper.toResponse(this.testCaseRepository.save(testCases));
        list.add(response);
        listTestCaseResponse.setProblemResponse(this.exchangeEntity(problem));
        listTestCaseResponse.setTestCasesResponse(list);
        return listTestCaseResponse;
    }

    @Override
    public ListTestCaseResponse updateTestCase(Long testCaseId, TestCasesDTO testCasesDTO) {
        TestCases testCases = this.findEntity(testCaseId);
        if (testCasesDTO.getInput()!=null
                && testCasesDTO.getOutput()!=null){
            testCases.setInput(Base64Util.encodeBase64(testCasesDTO.getInput()));
            testCases.setInput(Base64Util.encodeBase64(testCasesDTO.getOutput()));
        }
        if (testCasesDTO.getProblemId()!=null){
            testCases.setProblem(this.getProblem(testCasesDTO.getProblemId()));
        }
        if (testCasesDTO.getIsActive()!=null){
            testCases.setIsActive(testCasesDTO.getIsActive());
        }
        List<TestCasesResponse> list = new ArrayList<>();
        ListTestCaseResponse listTestCaseResponse = new ListTestCaseResponse();
        TestCasesResponse response = mapper.toResponse(this.testCaseRepository.save(testCases));
        list.add(response);
        listTestCaseResponse.setProblemResponse(this.exchangeEntity(testCases.getProblem()));
        listTestCaseResponse.setTestCasesResponse(list);
        return listTestCaseResponse;
    }

    @Override
    public ListTestCaseResponse deleteByTestCaseId(Long testCaseId) {
        TestCases testCases = this.findEntity(testCaseId);
        testCases.setIsActive(false);
        List<TestCasesResponse> list = new ArrayList<>();
        ListTestCaseResponse listTestCaseResponse = new ListTestCaseResponse();
        TestCasesResponse response = mapper.toResponse(this.testCaseRepository.save(testCases));
        list.add(response);
        listTestCaseResponse.setProblemResponse(this.exchangeEntity(testCases.getProblem()));
        listTestCaseResponse.setTestCasesResponse(list);
        return listTestCaseResponse;
    }

    @Override
    public ListTestCaseResponse deleteAllTestCase(Long problemId) {
        List<TestCases> list = this.testCaseRepository.findByProblemIdAndIsActiveOrderByTestCaseOrderAsc(problemId,  true);
        if (list.isEmpty()) {
            return ListTestCaseResponse.builder()
                    .testCasesResponse(Collections.emptyList())
                    .problemResponse(null)
                    .build();
        }
        list.forEach(items -> items.setIsActive(false));
        List<TestCasesResponse> responses = this.testCaseRepository.saveAll(list)
                .stream()
                .map(items -> {
                    TestCases testCase = new TestCases(items);
                    testCase.setInput(items.getInput());
                    testCase.setOutput(items.getOutput());
                    items.setInput(Base64Util.decodeBase64(testCase.getInput()));
                    items.setOutput(Base64Util.decodeBase64(testCase.getOutput()));
                    return mapper.toResponse(items);
                })
                .toList();
        ProblemResponse problemResponse = this.exchangeEntity(list.get(0).getProblem());
        return ListTestCaseResponse.builder()
                .testCasesResponse(responses)
                .problemResponse(problemResponse)
                .build();
    }

    @Override
    public ListTestCaseResponse getListTestCaseByProblemId(Long problemId) {
        List<TestCases> list = this.testCaseRepository.findByProblemIdAndIsActiveOrderByTestCaseOrderAsc(problemId, true);
        if (list.isEmpty()) {
            return ListTestCaseResponse.builder()
                    .testCasesResponse(Collections.emptyList())
                    .problemResponse(null)
                    .build();
        }

        List<TestCasesResponse> responses = list
                .stream()
                .map(mapper::toResponse)
                .toList();

        ProblemResponse problemResponse = this.exchangeEntity(list.get(0).getProblem());
        return ListTestCaseResponse.builder()
                .testCasesResponse(responses)
                .problemResponse(problemResponse)
                .build();
    }

    @Override
    public ListTestCaseResponse getTestCaseByProblemIdAndOrder(Long problemId, Long orderId) {
        TestCases testCases = this.testCaseRepository.findByProblemIdAndTestCaseOrder(problemId, orderId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));

        List<TestCasesResponse> list = new ArrayList<>();
        list.add(mapper.toResponse(testCases));

        return ListTestCaseResponse.builder()
                .problemResponse(this.exchangeEntity(testCases.getProblem()))
                .testCasesResponse(list)
                .build();
    }

    @Override
    public ListTestCaseResponse getAllTestCase(Long problemId) {
        List<TestCases> testCases = this.testCaseRepository.findByProblemIdOrderByTestCaseOrderAsc(problemId);
        if (testCases.isEmpty()) {
            return ListTestCaseResponse.builder()
                    .testCasesResponse(Collections.emptyList())
                    .problemResponse(null)
                    .build();
        }

        List<TestCasesResponse> responses = testCases.stream()
                .map(mapper::toResponse)
                .toList();

        ProblemResponse problemResponse = this.exchangeEntity(testCases.get(0).getProblem());
        return ListTestCaseResponse.builder()
                .testCasesResponse(responses)
                .problemResponse(problemResponse)
                .build();
    }

    @Override
    public TestCase mappingEntityToValidTestCase(TestCases testCases) {
        return TestCase.builder()
                .input(testCases.getInput())
                .expectedOutput(testCases.getOutput())
                .build();
    }

    @Override
    public TestCase mappingResponseToValidTestCase(TestCasesResponse testCasesResponse) {
        return TestCase.builder()
                .input(testCasesResponse.getInput())
                .expectedOutput(testCasesResponse.getOutput())
                .build();
    }
    private Problem getProblem(Long problemId){
        return this.problemRepository.findById(problemId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public final ProblemResponse exchangeEntity(Problem problem){
        return ProblemResponse.builder()
                .title(problem.getTitle())
                .points(problem.getPoints())
                .description(problem.getDescription())
                .difficult(problem.getDifficult())
                .category(problem.getCategory())
                .build();
    }
    private final TestCases findEntity(Long id){
        return this.testCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public final List<TestCase> getTestCaseDataService(Long problemId){
        System.out.println("X2");
        ListTestCaseResponse list = this.getListTestCaseByProblemId(problemId);
        System.out.println(list.getTestCasesResponse().size()+"SIZE");
        return list.getTestCasesResponse().stream()
                .map(this::toRealEntity)
                .toList();
    }
    private final TestCase toRealEntity(TestCasesResponse testCases){
        System.out.println(testCases.getInput()+" INPUT");
        System.out.println(testCases.getOutput() +" OUTPUT");
        return TestCase.builder()
                .input(testCases.getInput())
                .expectedOutput(testCases.getOutput())
                .build();
    }
    public void encodeAllTestCase(Long problemId){
        List<TestCases> listNonBase64 = this.testCaseRepository.findByProblemIdOrderByTestCaseOrderAsc(problemId);
        for (TestCases testCases: listNonBase64){
            testCases.setInput(Base64Util.encodeBase64(testCases.getInput()));
            testCases.setOutput(Base64Util.encodeBase64(testCases.getOutput()));
            testCases.setIsActive(true);
        }
        this.testCaseRepository.saveAll(listNonBase64);
    }
    public List<ExampleTestCasesResponse> getExampleTestCase(int limit, Long problemId){
        Pageable pageable = PageRequest.of(0, limit);
        List<TestCases> list = this.testCaseRepository.findTestCasesByProblemIdAndLimit(problemId, pageable);
        if (list.isEmpty()){
            return new ArrayList<>();
        }
        return list.stream()
                .map(this.mapper::toResponseEx)
                .collect(Collectors.toList());
    }
}
