package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.UserSessionDTO;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserSession;

import java.util.List;

public interface IUserSessionService {
    UserSession createSession(UserSessionDTO sessionDTO, User user);
    UserSession getSessionBySessionId(String sessionId);
    List<UserSessionDTO> getSessionsByUserId(Long userId);
    List<UserSessionDTO> getActiveSessions();
    List<UserSessionDTO> getExpiredSessions();
    void deleteSession(String sessionId);
    void deactivateSession(String sessionId);
    long countSessionsByUserId(Long userId);
    void cleanupExpiredSessions();
}
