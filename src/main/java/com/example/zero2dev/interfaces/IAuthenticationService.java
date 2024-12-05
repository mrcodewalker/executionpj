package com.example.zero2dev.interfaces;

import com.example.zero2dev.models.RefreshToken;
import com.example.zero2dev.models.User;
import com.example.zero2dev.responses.AuthenticationResponse;

public interface IAuthenticationService {
    AuthenticationResponse login(User user);
    AuthenticationResponse refreshToken(String refreshToken);
    void logout();
    boolean validateRefreshToken(String rawToken, RefreshToken storedToken);
}
