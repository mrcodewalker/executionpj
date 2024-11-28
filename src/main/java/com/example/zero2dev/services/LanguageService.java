package com.example.zero2dev.services;

import com.example.zero2dev.dtos.LanguageDTO;
import com.example.zero2dev.exceptions.DuplicateVersionException;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.ILanguageService;
import com.example.zero2dev.mapper.LanguageMapper;
import com.example.zero2dev.models.Language;
import com.example.zero2dev.repositories.LanguageRepository;
import com.example.zero2dev.responses.LanguageResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class LanguageService implements ILanguageService {
    private final LanguageRepository languageRepository;
    private final LanguageMapper mapper;
    @Override
    @Transactional
    public LanguageResponse createLanguage(LanguageDTO languageDTO) {
        this.checkExistVersion(languageDTO);
        Language language = this.languageRepository.save(mapper.toEntity(languageDTO));
        return mapper.toResponse(language);
    }

    @Override
    public LanguageResponse getLanguageById(Long id) {
        return mapper.toResponse(this.findLanguageById(id));
    }

    @Override
    public List<LanguageResponse> getAllLanguage() {
        return this.languageRepository.findAll().stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public LanguageResponse deleteById(Long id) {
        Language language = this.findLanguageById(id);
        language.setIsActive(false);
        return mapper.toResponse(this.languageRepository.save(language));
    }

    @Override
    public LanguageResponse updateLanguage(Long id, LanguageDTO languageDTO) {
        Language language = this.findLanguageById(id);
        mapper.updateLanguageFromDTO(languageDTO, language);
        this.checkExistVersion(languageDTO);
        return mapper.toResponse(this.languageRepository.save(language));
    }
    public Language findLanguageById(Long id){
        return this.languageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find language"));
    }
    public Language findLanguageByVersion(String version){
        Language language = this.languageRepository.findByVersion(version);
        if (language==null){
            throw new DuplicateVersionException("A version has been existed");
        }
        return language;
    }
    public boolean existByVersion(String version){
        Language language = this.languageRepository.findByVersion(version);
        return language != null;
    }
    public void checkExistVersion(LanguageDTO languageDTO){
        if (this.existByVersion(languageDTO.getVersion())){
            throw new DuplicateVersionException("A version has been existed!");
        }
    }
}
