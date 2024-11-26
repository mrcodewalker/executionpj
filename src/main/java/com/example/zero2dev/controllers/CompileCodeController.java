package com.example.zero2dev.controllers;

import com.example.zero2dev.dtos.CompileCodeDTO;
import com.example.zero2dev.responses.CompileCodeResponse;
import com.example.zero2dev.responses.ListCompileCodeResponse;
import com.example.zero2dev.services.CompileCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/compile")
public class CompileCodeController {
    private final CompileCodeService compileCodeService;
    @PostMapping("/submit")
    public ResponseEntity<ListCompileCodeResponse> submitCode(
            @RequestBody CompileCodeDTO compileCodeDTO) {
        return ResponseEntity.ok(
                this.compileCodeService.compileCode(
                        compileCodeDTO
                )
        );
    }
}
