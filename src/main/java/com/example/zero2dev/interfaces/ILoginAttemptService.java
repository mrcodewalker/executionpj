package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.LoginDTO;
import com.example.zero2dev.storage.LoginStatus;

public interface ILoginAttemptService {
    void recordLoginAttempt(LoginDTO loginDTO, LoginStatus status);
    boolean isAccountLocked(String username, String ipAddress);
}
