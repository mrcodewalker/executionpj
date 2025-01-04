package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.BlacklistedTokenDTO;
import com.example.zero2dev.models.BlacklistedToken;
import com.example.zero2dev.models.User;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;

import java.util.List;
import java.util.Optional;

public interface IBlacklistedTokenService {
    BlacklistedToken createNewRecord(BlacklistedTokenDTO blacklistedTokenDTO, String username);
    boolean isTokenBlacklisted(String token);
    Optional<BlacklistedToken> getBlacklistedTokenDetails(String token);
    List<BlacklistedToken> getUserBlacklistedTokens(String username);
    void removeUserBlacklistedTokens(String username);
}
