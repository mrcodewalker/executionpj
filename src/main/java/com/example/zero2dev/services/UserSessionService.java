package com.example.zero2dev.services;

import com.example.zero2dev.dtos.UserSessionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.interfaces.IUserSessionService;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserSession;
import com.example.zero2dev.repositories.UserSessionRepository;
import com.example.zero2dev.storage.MESSAGE;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserSessionService implements IUserSessionService {
    private final UserSessionRepository userSessionRepository;
    private final ModelMapper modelMapper;
    private final int sessionExpiredAfter;
    private final int maxSessionExists;
    public UserSessionService(
            UserSessionRepository userSessionRepository,
            ModelMapper mapper,
            @Value("${zero2dev.session_expired_after}") int sessionExpiredAfter,
            @Value("${zero2dev.max_session_exists}") int maxSessionExists){
        this.userSessionRepository = userSessionRepository;
        this.modelMapper = mapper;
        this.sessionExpiredAfter = sessionExpiredAfter;
        this.maxSessionExists = maxSessionExists;
    }
    @Override
    @Transactional
    public UserSession createSession(UserSessionDTO sessionDTO, User user) {
        List<UserSession> activeSessions = userSessionRepository.findActiveSessionsByUserId(user.getId());
        System.out.println(activeSessions.size()+"createSession 1 HAI DEP TRAI");
        if (activeSessions.size() >= this.maxSessionExists) {
            UserSession oldestSession = activeSessions.get(0);
            oldestSession.setIsActive(false);
            userSessionRepository.save(oldestSession);
        }

        UserSession userSession = modelMapper.map(sessionDTO, UserSession.class);
        userSession.setCreatedAt(LocalDateTime.now());
        userSession.setLastActiveAt(LocalDateTime.now());
        userSession.setSessionId(generateSecureSessionId(sessionDTO.getUserId()));
        userSession.setExpiredAt(LocalDateTime.now().plusHours(sessionExpiredAfter));
        System.out.println("createSession 2 HAI DEP TRAI");
        return userSessionRepository.save(userSession);
    }

    @Override
    public UserSession getSessionBySessionId(String sessionId) {
        return userSessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<UserSessionDTO> getSessionsByUserId(Long userId) {
        return userSessionRepository.findByUserId(userId)
                .stream()
                .map(session -> modelMapper.map(session, UserSessionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSessionDTO> getActiveSessions() {
        return userSessionRepository.findByIsActiveTrue()
                .stream()
                .map(session -> modelMapper.map(session, UserSessionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserSessionDTO> getExpiredSessions() {
        return userSessionRepository.findExpiredSessions(LocalDateTime.now())
                .stream()
                .map(session -> modelMapper.map(session, UserSessionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteSession(String sessionId) {
        userSessionRepository.deleteBySessionId(sessionId);
    }

    @Override
    @Transactional
    public void deactivateSession(String sessionId) {
        userSessionRepository.findBySessionId(sessionId)
                .ifPresent(session -> {
                    session.setIsActive(false);
                    userSessionRepository.save(session);
                });
    }

    @Override
    public long countSessionsByUserId(Long userId) {
        return userSessionRepository.countByUserId(userId);
    }

    @Override
    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanupExpiredSessions() {
        List<UserSession> expiredSessions = userSessionRepository.findExpiredSessions(LocalDateTime.now());
        userSessionRepository.deleteAll(expiredSessions);
    }
    public final String generateSecureSessionId(Long userId) {
        return UUID.randomUUID().toString() + "-" + userId;
    }
    public boolean isValidSession(String sessionId) {
        Optional<UserSession> sessionOpt = userSessionRepository.findBySessionId(sessionId);

        return sessionOpt.map(session ->
                session.getIsActive() &&
                        session.getExpiredAt().isAfter(LocalDateTime.now())
        ).orElse(false);
    }

    public Long invalidateSession(String sessionId) {
        Optional<UserSession> optionalUserSession = userSessionRepository.findBySessionId(sessionId);

        if (optionalUserSession.isEmpty()) {
            throw new ResourceNotFoundException(MESSAGE.IP_BLACKLISTED);
        }
        UserSession userSession = optionalUserSession.get();
        userSession.setIsActive(false);
        userSessionRepository.save(userSession);
        return userSession.getUserId();
    }
}