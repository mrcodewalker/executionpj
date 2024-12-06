package com.example.zero2dev.services;

import com.example.zero2dev.dtos.LoginDTO;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.ILoginAttemptService;
import com.example.zero2dev.models.BlacklistedIP;
import com.example.zero2dev.models.LoginAttempt;
import com.example.zero2dev.repositories.BlacklistedIPRepository;
import com.example.zero2dev.repositories.LoginAttemptRepository;
import com.example.zero2dev.storage.BlacklistStatus;
import com.example.zero2dev.storage.LoginStatus;
import com.example.zero2dev.storage.MESSAGE;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LoginAttemptService implements ILoginAttemptService {
    private final LoginAttemptRepository loginAttemptRepository;
    private final int timeBackTrack;
    private final int maxFailedLogin;
    private final IPSecurityService ipSecurityService;
    private final BlacklistedIPRepository blacklistedIPRepository;
    public LoginAttemptService(
            LoginAttemptRepository repository,
            @Value("${zero2dev.max_failed_login}") int maxFailedLogin,
            @Value("${zero2dev.time_back_track}") int timeBackTrack,
            IPSecurityService ipSecurityService,
            BlacklistedIPRepository blacklistedIPRepository) {
        this.loginAttemptRepository = repository;
        this.maxFailedLogin = maxFailedLogin;
        this.timeBackTrack = timeBackTrack;
        this.ipSecurityService = ipSecurityService;
        this.blacklistedIPRepository = blacklistedIPRepository;
    }
    @Override
    public void recordLoginAttempt(LoginDTO loginDTO, LoginStatus status, HttpServletRequest request) {
        LoginAttempt attempt = LoginAttempt.builder()
                .username(loginDTO.getUsername())
                .attemptTime(LocalDateTime.now())
                .status(status)
                .ipAddress(loginDTO.getIpAddress())
                .deviceInfo(IpService.generateDeviceInfoString(request))
                .build();
//        if (status.equals(LoginStatus.SUCCESS)){
//            this.unbanIP(loginDTO.getIpAddress());
//        }

        this.loginAttemptRepository.save(attempt);
    }

    @Override
    public boolean isAccountLocked(String username, String ipAddress) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(timeBackTrack);
        List<LoginAttempt> failedAttempts = loginAttemptRepository.findByUsernameAndIpAddressAndStatusAndAttemptTimeAfter(
                username,
                ipAddress,
                LoginStatus.FAILED,
                oneHourAgo
        );

        return failedAttempts.size() >= this.maxFailedLogin;
    }
    public void deleteLoginAttemptsByIpAndTimeRange(String ipAddress, LocalDateTime startTime) {
        loginAttemptRepository.deleteLoginAttemptsByIpAddressAndTimeRange(ipAddress, LoginStatus.FAILED, startTime);
    }
    public final void unbanIP(Long id){
        BlacklistedIP blacklistedIP = this.blacklistedIPRepository.findById(id)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        blacklistedIP.setStatus(BlacklistStatus.EXPIRED);
        this.blacklistedIPRepository.save(blacklistedIP);
        this.deleteLoginAttemptsByIpAndTimeRange(blacklistedIP.getIpAddress(),
                LocalDateTime.now().minusHours(timeBackTrack));
    }
    public final void unbanIP(String ipAddress){
        BlacklistedIP blacklistedIP = this.blacklistedIPRepository.findByIpAddress(ipAddress)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        blacklistedIP.setStatus(BlacklistStatus.EXPIRED);
        this.blacklistedIPRepository.save(blacklistedIP);
        this.deleteLoginAttemptsByIpAndTimeRange(blacklistedIP.getIpAddress(),
                LocalDateTime.now().minusHours(timeBackTrack));
    }
    @Scheduled(cron = "0 0 * * * ?")
    public void updateBlacklistedIPStatus() {
        List<BlacklistedIP> expiredEntries = blacklistedIPRepository
                .findByStatusAndUnblacklistAtBefore(
                        BlacklistStatus.ACTIVE,
                        LocalDateTime.now()
                );

        expiredEntries.forEach(entry -> {
            entry.setStatus(BlacklistStatus.EXPIRED);
            blacklistedIPRepository.save(entry);
            this.deleteLoginAttemptsByIpAndTimeRange(
                    entry.getIpAddress(),
                    LocalDateTime.now().minusHours(timeBackTrack));
        });
    }
//    @Scheduled(cron = "1 0 0 */7 * ?")
//    public void deleteAllLoginAttempts() {
//        loginAttemptRepository.deleteAll();
//        loginAttemptRepository.resetAutoIncrement();
//    }
}
