package com.example.zero2dev.interfaces;

import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserSession;
import com.example.zero2dev.responses.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface IAuthenticationService {
    AuthenticationResponse login(UserSession userSession, User user);
    AuthenticationResponse refreshToken(String refreshToken, HttpServletRequest request);
    void logout(String sessionId, HttpServletRequest request);
    boolean validateRefreshToken(String rawToken, RefreshToken storedToken);
}
