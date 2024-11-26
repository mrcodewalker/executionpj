package com.example.zero2dev.responses;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.models.Language;
import com.example.zero2dev.storage.CompilerVersion;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Arrays;

@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class LanguageResponse {
    @JsonProperty("name")
    private String name;
    @JsonProperty("version")
    private CompilerVersion compilerVersion;
    public static LanguageResponse exchangeEntity(Language language){
        boolean isValidVersion =
                Arrays.stream(CompilerVersion.values()).anyMatch(
                    version -> version.name().equals(language.getVersion().toUpperCase())
                );
        if(!isValidVersion){
            throw new ResourceNotFoundException("Can not found version");
        }
        return LanguageResponse.builder()
                .name(language.getName())
                .compilerVersion(CompilerVersion.valueOf(language.getVersion()))
                .build();
    }
}
