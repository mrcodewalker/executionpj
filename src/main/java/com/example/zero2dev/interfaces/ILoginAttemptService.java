package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.LoginDTO;
import com.example.zero2dev.storage.LoginStatus;
import jakarta.servlet.http.HttpServletRequest;

public interface ILoginAttemptService {
    void recordLoginAttempt(LoginDTO loginDTO, LoginStatus status, HttpServletRequest request);
    boolean isAccountLocked(String username, String ipAddress);
}
