package com.example.zero2dev.services;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.filter.JwtTokenProvider;
import com.example.zero2dev.interfaces.IAuthenticationService;
import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.Token;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.RefreshTokenRepository;
import com.example.zero2dev.responses.AuthenticationResponse;
import com.example.zero2dev.storage.MESSAGE;
import lombok.RequiredArgsConstructor;
import org.aspectj.bridge.Message;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtService;
    private final RefreshTokenRepository refreshTokenRepository;
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
    public void logout() {
        User user = SecurityService.getUserIdFromSecurityContext();
        if (user==null){
            throw new ValueNotValidException(MESSAGE.GENERAL_ERROR);
        }
        refreshTokenRepository.findByUser(user)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
        tokenService.revokeAllUserTokens(user);
    }
    @Override
    public boolean validateRefreshToken(String rawToken, RefreshToken storedToken) {
        if (storedToken.isRevoked()){
            throw new ValueNotValidException(MESSAGE.TOKEN_HAS_BEEN_REVOKED);
        }
        return passwordEncoder.matches(rawToken, storedToken.getToken());
    }
}