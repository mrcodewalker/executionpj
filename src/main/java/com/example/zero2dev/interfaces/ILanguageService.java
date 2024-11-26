package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.LanguageDTO;
import com.example.zero2dev.responses.LanguageResponse;
import com.example.zero2dev.storage.Language;

import java.util.List;

public interface ILanguageService {
    LanguageResponse createLanguage(LanguageDTO languageDTO);
    LanguageResponse getLanguageById(Long id);
    List<LanguageResponse> getAllLanguage();
    LanguageResponse deleteById(Long id);
    LanguageResponse updateLanguage(Long id, LanguageDTO languageDTO);
}
