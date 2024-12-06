package com.example.zero2dev.services;

import com.example.zero2dev.dtos.BlacklistedTokenDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.filter.JwtTokenProvider;
import com.example.zero2dev.interfaces.IAuthenticationService;
import com.example.zero2dev.models.BlacklistedToken;
import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.Token;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.RefreshTokenRepository;
import com.example.zero2dev.responses.AuthenticationResponse;
import com.example.zero2dev.responses.RefreshTokenResponse;
import com.example.zero2dev.storage.BlacklistReason;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.TokenType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistedTokenService blacklistedTokenService;
    @Override
    public AuthenticationResponse login(User user) {
        if (user==null){
            throw new ResourceNotFoundException(MESSAGE.FORBIDDEN_REQUEST);
        }
        String jwtToken = jwtService.generateToken(user);
        Token accessToken = tokenService.createAccessToken(user, jwtToken);
        RefreshToken refreshToken = tokenService.createRefreshToken(user);

        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken.getToken())
                .expiredAccessToken(accessToken.getExpiredAt())
                .expiredRefreshToken(refreshToken.getExpiredAt())
                .build();
    }
    @Override
    public AuthenticationResponse refreshToken(String refreshToken) {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user == null){
            throw new ValueNotValidException(MESSAGE.FORBIDDEN_REQUEST);
        }
        RefreshToken token = this.refreshTokenRepository.findByUser(user)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.REFRESH_TOKEN_EXPIRED));
        if (!this.validateRefreshToken(refreshToken, token)
                || token.isRevoked()){
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }
        if (token.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ValueNotValidException(MESSAGE.REFRESH_TOKEN_EXPIRED);
        }
        String newAccessToken = jwtService.generateToken(user);
        Token newToken = tokenService.createAccessToken(user, newAccessToken);

        return AuthenticationResponse.builder()
                .expiredAccessToken(newToken.getExpiredAt())
                .expiredRefreshToken(token.getExpiredAt())
                .accessToken(newAccessToken)
                .build();
    }
    @Override
    public void logout(HttpServletRequest request) {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }
        Optional<RefreshToken> refreshTokenOptional = refreshTokenRepository.findByUser(user);
        BlacklistedTokenDTO blacklistedTokenDTO = BlacklistedTokenDTO.builder()
                .ipAddress(IpService.getClientIp(request))
                .reason(BlacklistReason.USER_LOGOUT)
                .tokenType(TokenType.REFRESH)
                .deviceInfo(IpService.generateDeviceInfoString(request))
                .build();
        refreshTokenOptional.ifPresent(refreshToken -> {
            refreshToken.setRevoked(true);
            blacklistedTokenDTO.setToken(refreshToken.getToken());
            refreshTokenRepository.save(refreshToken);
        });
        List<Token> tokens = tokenService.revokeAllUserTokens(user);
        this.blacklistedTokenService.createNewRecord(blacklistedTokenDTO);
        List<BlacklistedToken> list = new ArrayList<>();
        for (Token token : tokens){
            blacklistedTokenDTO.setToken(token.getToken());
            blacklistedTokenDTO.setTokenType(token.getTokenType());
            list.add(blacklistedTokenService.exchangeEntity(blacklistedTokenDTO, user.getUsername()));
        }
        this.blacklistedTokenService.saveAllData(list);
    }
    @Override
    public boolean validateRefreshToken(String rawToken, RefreshToken storedToken) {
        if (storedToken.isRevoked()){
            throw new ValueNotValidException(MESSAGE.TOKEN_HAS_BEEN_REVOKED);
        }
        return passwordEncoder.matches(rawToken, storedToken.getToken());
    }
    public final List<RefreshTokenResponse> filterList(){
        return Optional.of(refreshTokenRepository.findAll())
                .filter(items -> !items.isEmpty())
                .map(items -> items.stream()
                        .map(this::exchangeResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private RefreshTokenResponse exchangeResponse(RefreshToken refreshToken){
        return RefreshTokenResponse.builder()
                .id(refreshToken.getId())
                .createdAt(refreshToken.getCreatedAt())
                .expiredAt(refreshToken.getExpiredAt())
                .revoked(refreshToken.isRevoked())
                .username(refreshToken.getUser().getUsername())
                .updatedAt(refreshToken.getUpdatedAt())
                .token(refreshToken.getToken())
                .build();
    }
}