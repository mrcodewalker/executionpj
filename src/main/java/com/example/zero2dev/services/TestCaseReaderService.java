package com.example.zero2dev.services;

import com.example.zero2dev.dtos.TestCaseReaderDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ITestCaseReaderService;
import com.example.zero2dev.mapper.TestCaseReaderMapper;
import com.example.zero2dev.models.Problem;
import com.example.zero2dev.models.TestCase;
import com.example.zero2dev.models.TestCaseReader;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.TestCaseReaderRepository;
import com.example.zero2dev.responses.ListTestCaseReaderResponse;
import com.example.zero2dev.responses.ProblemResponse;
import com.example.zero2dev.responses.TestCaseReaderResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TestCaseReaderService implements ITestCaseReaderService {
    private final TestCaseReaderRepository testCaseReaderRepository;
    private final TestCaseReaderMapper mapper;
    private final ProblemService problemService;
    @Override
    public TestCaseReaderResponse createTestCase(TestCaseReaderDTO testCaseReaderDTO) {
        Problem problem = this.problemService.findProblemById(testCaseReaderDTO.getProblemId());
        TestCaseReader testCaseReader = mapper.toEntity(testCaseReaderDTO);
        testCaseReader.setIsActive(true);
        testCaseReader.setProblem(problem);
        return TestCaseReaderResponse.exchangeEntity(this.testCaseReaderRepository.save(testCaseReader));
    }

    @Override
    public TestCaseReaderResponse updateTestCase(Long id, TestCaseReaderDTO testCaseReaderDTO) {
        TestCaseReader testCaseReader = this.newTestCaseReader(id, testCaseReaderDTO);
        String newInputPath = (testCaseReaderDTO.getInputPath()!=null) ? testCaseReaderDTO.getInputPath() : testCaseReader.getInputPath();
        String newOutputPath = (testCaseReaderDTO.getOutputPath()!=null) ? testCaseReaderDTO.getOutputPath() : testCaseReader.getOutputPath();
        boolean newIsActive = (testCaseReaderDTO.isActive()!=testCaseReader.getIsActive())
                ? testCaseReaderDTO.isActive() : testCaseReader.getIsActive();

        testCaseReader.setInputPath(newInputPath);
        testCaseReader.setOutputPath(newOutputPath);
        testCaseReader.setIsActive(newIsActive);

        return TestCaseReaderResponse.exchangeEntity(
                this.testCaseReaderRepository.save(testCaseReader));
    }

    @Override
    public TestCaseReaderResponse deleteTestCase(Long id) {
        TestCaseReader testCaseReader = this.findTestCaseReaderById(id);
        testCaseReader.setIsActive(false);
        this.testCaseReaderRepository.save(testCaseReader);
        return TestCaseReaderResponse.exchangeEntity(testCaseReader);
    }

    @Override
    public ListTestCaseReaderResponse getListTestCase(Long problemId) {
        Problem problem = this.problemService.findProblemById(problemId);
        List<TestCaseReader> testCases = this.testCaseReaderRepository.getByProblemId(problemId);
        return ListTestCaseReaderResponse.exchangeEntity(testCases, problem);
    }

    @Override
    public TestCaseReaderResponse getTestCaseById(Long id) {
        TestCaseReader testCaseReader = this.findTestCaseReaderById(id);
        return TestCaseReaderResponse.exchangeEntity(testCaseReader);
    }
    public TestCaseReader findTestCaseReaderById(Long id){
        return this.testCaseReaderRepository
                .findById(id).orElseThrow(()->new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    public TestCaseReader newTestCaseReader(Long id, TestCaseReaderDTO testCaseReaderDTO){
        if (testCaseReaderDTO==null){
            throw new ResourceNotFoundException("Can not update right now");
        }
        TestCaseReader testCaseReader = this.findTestCaseReaderById(id);
        if(!testCaseReader.getProblem().getId().equals(testCaseReaderDTO.getProblemId())) {
            Problem problem = this.problemService.findProblemById(testCaseReaderDTO.getProblemId());
            testCaseReader.setProblem(problem);
        }
        return testCaseReader;
    }
    public List<TestCase> getTestCases(Long problemId) {
        Problem problem = this.problemService.findProblemById(problemId);
        List<TestCaseReader> testCases = this.testCaseReaderRepository.getByProblemId(problemId);
        List<TestCase> processedTestCases = new ArrayList<>();
        for (TestCaseReader items : testCases){
            try {
                String inputContent = readFileContent(items.getInputPath());

                String outputContent = readFileContent(items.getOutputPath());

                TestCase testCase = new TestCase(inputContent, outputContent);

                processedTestCases.add(testCase);
            } catch (IOException e) {
                throw new RuntimeException(MESSAGE.GENERAL_ERROR, e);
            }
        }
        return processedTestCases;
    }
    private String readFileContent(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        return Files.readString(path);
    }
}
