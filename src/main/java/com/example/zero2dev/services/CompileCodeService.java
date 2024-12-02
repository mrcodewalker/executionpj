package com.example.zero2dev.services;

import com.example.zero2dev.dtos.CompileCodeDTO;
import com.example.zero2dev.exceptions.CompileException;
import com.example.zero2dev.interfaces.ICompileCodeService;
import com.example.zero2dev.responses.CompileCodeResponse;
import com.example.zero2dev.responses.ListCompileCodeResponse;
import com.example.zero2dev.responses.TestCaseResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompileCodeService implements ICompileCodeService {
    private final RestTemplate restTemplate;
    private final String dockerServerUrl = "http://localhost:8888/submit";
    private final ObjectMapper objectMapper;
    @Override
    public ListCompileCodeResponse compileCode(CompileCodeDTO compileCodeDTO) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<CompileCodeDTO> request = new HttpEntity<>(compileCodeDTO, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    dockerServerUrl,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                List<CompileCodeResponse> compileCodeResponses = objectMapper.readValue(
                        response.getBody(),
                        new TypeReference<List<CompileCodeResponse>>() {}
                );

                ListCompileCodeResponse compileResponse = new ListCompileCodeResponse();
                compileResponse.setCompileCodeResponses(compileCodeResponses);

                long totalExecutionTime = compileCodeResponses.stream()
                        .mapToLong(CompileCodeResponse::getExecutionTime)
                        .sum();
                long totalMemoryUsed = compileCodeResponses.stream()
                        .mapToLong(CompileCodeResponse::getMemoryUsed)
                        .sum();


                boolean allTestsPassed = true;
                long failedAt = 0;

                for (CompileCodeResponse compileCodeResponse: compileCodeResponses) {
                    if (!compileCodeResponse.isPassed) {
                        allTestsPassed = false;
                        break;
                    }
                    failedAt++;
                }

                if (allTestsPassed){
                    failedAt = -1;
                }

                compileResponse.setTotalExecutionTime(totalExecutionTime);
                compileResponse.setAllTestsPassed(allTestsPassed);
                compileResponse.setFailedAt(failedAt+1);
                compileResponse.setTotalTests(compileCodeDTO.getTestCases().size());
                compileResponse.setTotalMemoryUsed(totalMemoryUsed);

                return compileResponse;
            } else {
                throw new CompileException("Failed to compile code: " + response.getStatusCode());
            }

        } catch (JsonProcessingException e) {
            throw new CompileException("Error parsing compile results: " + e.getMessage());
        } catch (Exception e) {
            throw new CompileException("Error while compiling code: " + e.getMessage());
        }
    }
}
