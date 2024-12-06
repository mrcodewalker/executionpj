package com.example.zero2dev.services;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.ITokenService;
import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.Token;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.RefreshTokenRepository;
import com.example.zero2dev.repositories.TokenRepository;
import com.example.zero2dev.responses.TokenResponse;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TokenService implements ITokenService {
    private final PasswordEncoder passwordEncoder;
    private final TokenRepository tokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final int accessTokenExpiredAfter;
    private final int refreshTokenExpiredAfter;
    public TokenService(
            PasswordEncoder passwordEncoder,
            TokenRepository tokenRepository,
            RefreshTokenRepository refreshTokenRepository,
            @Value("${zero2dev.access_token_expired_after}") int accessTokenExpiredAfter,
            @Value("${zero2dev.refresh_token_expired_after}") int refreshTokenExpiredAfter){
        this.passwordEncoder = passwordEncoder;
        this.tokenRepository = tokenRepository;
        this.refreshTokenExpiredAfter = refreshTokenExpiredAfter;
        this.refreshTokenRepository = refreshTokenRepository;
        this.accessTokenExpiredAfter = accessTokenExpiredAfter;
    }

    @Override
    public Token createAccessToken(User user, String jwtToken) {
        Token token = this.tokenRepository.findByUserAndTokenType(user, TokenType.ACCESS)
                .orElse(new Token());
        token.setToken(jwtToken);
        token.setUser(user);
        token.setRevoked(false);
        token.setExpired(false);
        token.setTokenType(TokenType.ACCESS);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(this.accessTokenExpiredAfter));
        return tokenRepository.save(token);
    }

    @Override
    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = this.refreshTokenRepository.findByUser(user)
                .orElse(new RefreshToken());
        RefreshToken faker = new RefreshToken();
        String uniqueCode = generateUniqueRefreshToken();
        faker.setToken(uniqueCode);
        refreshToken.setToken(passwordEncoder.encode(uniqueCode));
        refreshToken.setUser(user);
        refreshToken.setRevoked(false);
        refreshToken.setExpiredAt(LocalDateTime.now().plusDays(this.refreshTokenExpiredAfter));
        refreshTokenRepository.save(refreshToken);
        faker.setUser(user);
        faker.setExpiredAt(refreshToken.getExpiredAt());
        return faker;
    }

    @Override
    public List<Token> revokeAllUserTokens(User user) {
        List<Token> validTokens = tokenRepository.findAllValidTokensByUser(user.getId());

        validTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });

        tokenRepository.saveAll(validTokens);
        return validTokens;
    }

    @Override
    public boolean validateToken(String token, TokenType tokenType) {
        Optional<Token> existingToken = tokenRepository.findByToken(token);

        return existingToken.map(t ->
                !t.isExpired() &&
                        !t.isRevoked() &&
                        t.getExpiredAt().isAfter(LocalDateTime.now())
        ).orElse(false);
    }

    @Override
    public List<TokenResponse> filterToken() {
        return Optional.of(this.tokenRepository.findAll())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(this::exchangeResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    public boolean validateRefreshToken(String rawToken, RefreshToken storedToken) {
        return passwordEncoder.matches(rawToken, storedToken.getToken());
    }
    public boolean validateToken(String rawToken, Token token){
        return passwordEncoder.matches(rawToken, token.getToken());
    }
    private TokenResponse exchangeResponse(Token token){
        return TokenResponse.builder()
                .id(token.getId())
                .createdAt(token.getCreatedAt())
                .expiredAt(token.getExpiredAt())
                .updatedAt(token.getUpdatedAt())
                .token(token.getToken())
                .tokenType(token.getTokenType())
                .expired(token.isExpired())
                .revoked(token.isRevoked())
                .username(token.getUser().getUsername())
                .build();
    }
    public String generateUniqueRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
