package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.CompileCodeDTO;
import com.example.zero2dev.responses.ListCompileCodeResponse;

public interface ICompileCodeService {
    ListCompileCodeResponse compileCode(CompileCodeDTO compileCodeDTO);
}
