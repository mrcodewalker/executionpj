package com.example.zero2dev.services;

import com.example.zero2dev.models.BlacklistedIP;
import com.example.zero2dev.repositories.BlacklistedIPRepository;
import com.example.zero2dev.repositories.LoginAttemptRepository;
import com.example.zero2dev.storage.BlacklistReason;
import com.example.zero2dev.storage.BlacklistStatus;
import com.example.zero2dev.storage.LoginStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class IPSecurityService {
    private final LoginAttemptRepository loginAttemptRepository;

    private final BlacklistedIPRepository blacklistedIPRepository;

    private final int maxFailedLogin;
    private final int timeBackTrack;
    private final int unbanAfter;
    public IPSecurityService(
            LoginAttemptRepository repository,
            BlacklistedIPRepository blacklistedIPRepository,
            @Value("${zero2dev.max_failed_login}") int maxFailedLogin,
            @Value("${zero2dev.time_back_track}") int timeBackTrack,
            @Value("${zero2dev.unban_after_hour}") int unbanAfter) {
        this.loginAttemptRepository = repository;
        this.blacklistedIPRepository = blacklistedIPRepository;
        this.maxFailedLogin = maxFailedLogin;
        this.timeBackTrack = timeBackTrack;
        this.unbanAfter = unbanAfter;
    }

    public boolean isIPBlacklisted(String ipAddress) {
        return blacklistedIPRepository.existsByIpAddressAndStatusAndUnblacklistAtAfter(
                ipAddress,
                BlacklistStatus.ACTIVE,
                LocalDateTime.now()
        );
    }
    public boolean isIPUnban(String ipAddress){
        return blacklistedIPRepository.existsByIpAddressAndStatusAndUnblacklistAtAfter(
                ipAddress,
                BlacklistStatus.EXPIRED,
                LocalDateTime.now()
        );
    }
    public void createIPBlackList(HttpServletRequest request){
        BlacklistedIP blacklistEntry = this.blacklistedIPRepository.findByIpAddress(
                IpService.getClientIp(request)
        ).orElse(new BlacklistedIP());
        blacklistEntry.setIpAddress(IpService.getClientIp(request));
        blacklistEntry.setBlacklistedAt(LocalDateTime.now());
        blacklistEntry.setStatus(BlacklistStatus.ACTIVE);
        blacklistEntry.setUnblacklistAt(LocalDateTime.now().plusHours(this.unbanAfter));
        blacklistEntry.setReason("Excessive login attempts");
        blacklistEntry.setDeviceInfo(IpService.generateDeviceInfoString(request));
        blacklistedIPRepository.save(blacklistEntry);
    }
    public void banIPByAdmin(String ipAddress){
        BlacklistedIP blacklistEntry = this.blacklistedIPRepository.findByIpAddress(
                ipAddress
        ).orElse(new BlacklistedIP());
        blacklistEntry.setIpAddress(ipAddress);
        blacklistEntry.setBlacklistedAt(LocalDateTime.now());
        blacklistEntry.setStatus(BlacklistStatus.ACTIVE);
        blacklistEntry.setUnblacklistAt(LocalDateTime.now().plusHours(this.unbanAfter));
        blacklistEntry.setReason("Excessive login attempts");
        if (blacklistEntry.getDeviceInfo()==null) {
            blacklistEntry.setDeviceInfo("Unknown");
        }
        blacklistedIPRepository.save(blacklistEntry);
    }
}
