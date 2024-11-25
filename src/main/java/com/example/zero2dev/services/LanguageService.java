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
    private final LanguageMapper languageMapper;
    @Override
    @Transactional
    public LanguageResponse createLanguage(LanguageDTO languageDTO) {
        boolean existingLanguage = this.existByVersion(languageDTO.getVersion());
        if (existingLanguage){
            throw new DuplicateVersionException("A version has been existed!");
        }
        Language language = this.languageRepository.save(
                LanguageDTO.exchangeEntity(languageDTO)
        );
        return LanguageResponse.exchangeEntity(language);
    }

    @Override
    public LanguageResponse getLanguageById(Long id) {
        Language language = this.findLanguageById(id);
        return LanguageResponse.exchangeEntity(language);
    }

    @Override
    public List<LanguageResponse> getAllLanguage() {
        List<Language> languages = this.languageRepository.findAll();
        return languages.stream()
                .map(LanguageResponse::exchangeEntity)
                .toList();
    }

    @Override
    public LanguageResponse deleteById(Long id) {
        Language language = this.findLanguageById(id);
        language.setIsActive(false);
        return LanguageResponse.exchangeEntity(this.languageRepository.save(language));
    }

    @Override
    public LanguageResponse updateLanguage(Long id, LanguageDTO languageDTO) {
        Language language = this.findLanguageById(id);
        languageMapper.updateLanguageFromDTO(languageDTO, language);
        if (this.existByVersion(language.getVersion())){
            throw new DuplicateVersionException("A version has been existed");
        }
        Language updatedLanguage = this.languageRepository.save(language);
        return LanguageResponse.exchangeEntity(updatedLanguage);
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
}
