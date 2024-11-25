package com.example.zero2dev.dtos;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.models.Language;
import com.example.zero2dev.storage.CompilerVersion;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LanguageDTO {
    private String name;
    @JsonProperty("version")
    private String version;
    public static Language exchangeEntity(LanguageDTO languageDTO) {
        if (languageDTO.getVersion() == null || languageDTO.getVersion().trim().isEmpty()) {
            throw new ResourceNotFoundException("Version is missing or empty");
        }

        String versionTrimmed = languageDTO.getVersion().trim().toUpperCase();
        boolean isValidVersion = Arrays.stream(CompilerVersion.values())
                .anyMatch(version -> version.name().equalsIgnoreCase(versionTrimmed));

        if (!isValidVersion) {
            throw new ResourceNotFoundException("Cannot find version: " + languageDTO.getVersion());
        }

        return Language.builder()
                .isActive(true)
                .name(languageDTO.getName())
                .version(versionTrimmed.toUpperCase())
                .build();
    }

}
