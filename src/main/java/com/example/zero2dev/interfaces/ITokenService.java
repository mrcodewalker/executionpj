package com.example.zero2dev.interfaces;

import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.Token;
import com.example.zero2dev.models.User;
import com.example.zero2dev.storage.TokenType;

public interface ITokenService {
    Token createAccessToken(User user, String jwtToken);
    RefreshToken createRefreshToken(User user);
    void revokeAllUserTokens(User user);
    boolean validateToken(String token, TokenType tokenType);
}
