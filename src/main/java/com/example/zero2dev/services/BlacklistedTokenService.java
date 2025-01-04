package com.example.zero2dev.services;

import com.example.zero2dev.dtos.BlacklistedTokenDTO;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.IBlacklistedTokenService;
import com.example.zero2dev.models.BlacklistedToken;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.BlacklistedIPRepository;
import com.example.zero2dev.repositories.BlacklistedTokenRepository;
import com.example.zero2dev.storage.BlacklistStatus;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlacklistedTokenService implements IBlacklistedTokenService {
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    @Override
    public BlacklistedToken createNewRecord(BlacklistedTokenDTO blacklistedTokenDTO, String userName) {
        return this.blacklistedTokenRepository.save(this.exchangeEntity(blacklistedTokenDTO, userName));
    }
    @Override
    public boolean isTokenBlacklisted(String token) {
        return this.blacklistedTokenRepository.existsByToken(token);
    }
    @Override
    public Optional<BlacklistedToken> getBlacklistedTokenDetails(String token) {
        return this.blacklistedTokenRepository.findByToken(token);
    }

    @Override
    public List<BlacklistedToken> getUserBlacklistedTokens(String username) {
        return this.blacklistedTokenRepository.findByUsername(username);
    }

    @Override
    public void removeUserBlacklistedTokens(String username) {
        this.blacklistedTokenRepository.deleteByUsername(username);
    }
    public final void saveAllData(List<BlacklistedToken> blacklistedTokens){
        this.blacklistedTokenRepository.saveAll(blacklistedTokens);
    }
    public final BlacklistedToken exchangeEntity(BlacklistedTokenDTO blacklistedTokenDTO,
                                                 String username){
        return BlacklistedToken.builder()
                .tokenType(blacklistedTokenDTO.getTokenType())
                .ipAddress(blacklistedTokenDTO.getIpAddress())
                .deviceInfo(blacklistedTokenDTO.getDeviceInfo())
                .reason(blacklistedTokenDTO.getReason())
                .token(blacklistedTokenDTO.getToken())
                .username(username)
                .build();
    }
    public final List<BlacklistedToken> filterList(){
        return this.blacklistedTokenRepository.findAll();
    }
}
