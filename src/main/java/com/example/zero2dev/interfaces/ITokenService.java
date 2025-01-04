package com.example.zero2dev.interfaces;

import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.Token;
import com.example.zero2dev.models.User;
import com.example.zero2dev.responses.TokenResponse;
import com.example.zero2dev.services.TokenService;
import com.example.zero2dev.storage.TokenType;

import java.util.List;

public interface ITokenService {
    Token createAccessToken(User user, String jwtToken);
    RefreshToken createRefreshToken(User user);
    List<Token> revokeAllUserTokens(Long userId);
    boolean validateToken(String token, TokenType tokenType);
    List<TokenResponse> filterToken();
}
