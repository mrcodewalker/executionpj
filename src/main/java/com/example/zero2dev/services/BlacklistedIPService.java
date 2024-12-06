package com.example.zero2dev.services;

import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.models.BlacklistedIP;
import com.example.zero2dev.models.BlacklistedToken;
import com.example.zero2dev.repositories.BlacklistedIPRepository;
import com.example.zero2dev.storage.BlacklistStatus;
import com.example.zero2dev.storage.MESSAGE;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlacklistedIPService {
    private final BlacklistedIPRepository repository;
    private final IPSecurityService ipSecurityService;
    private final LoginAttemptService loginAttemptService;
    private final int timeBackTrack;
    public BlacklistedIPService(
            BlacklistedIPRepository blacklistedIPRepository,
            IPSecurityService ipSecurityService,
            LoginAttemptService loginAttemptService,
            @Value("${zero2dev.time_back_track}") int timeBackTrack){
        this.ipSecurityService = ipSecurityService;
        this.repository = blacklistedIPRepository;
        this.loginAttemptService = loginAttemptService;
        this.timeBackTrack = timeBackTrack;
    }
    public final List<BlacklistedIP> filterList(){
        return this.repository.findAll();
    }
    public final void unbanIP(Long id){
        BlacklistedIP blacklistedIP = this.repository.findById(id)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        blacklistedIP.setStatus(BlacklistStatus.EXPIRED);
        this.repository.save(blacklistedIP);
        this.loginAttemptService.deleteLoginAttemptsByIpAndTimeRange(blacklistedIP.getIpAddress(),
                LocalDateTime.now().minusHours(timeBackTrack));
    }
    public final void unbanIP(String ipAddress){
        BlacklistedIP blacklistedIP = this.repository.findByIpAddress(ipAddress)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        blacklistedIP.setStatus(BlacklistStatus.EXPIRED);
        this.repository.save(blacklistedIP);
        this.loginAttemptService.deleteLoginAttemptsByIpAndTimeRange(blacklistedIP.getIpAddress(),
                LocalDateTime.now().minusHours(timeBackTrack));
    }
    public final void banIP(String ipAddress){
        BlacklistedIP blacklistedIP = this.repository.findByIpAddress(ipAddress)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        blacklistedIP.setStatus(BlacklistStatus.ACTIVE);
        this.ipSecurityService.banIPByAdmin(ipAddress);
    }
    public final void banIP(Long id){
        BlacklistedIP blacklistedIP = this.repository.findById(id)
                .orElseThrow(() -> new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
        blacklistedIP.setStatus(BlacklistStatus.ACTIVE);
        ipSecurityService.banIPByAdmin(blacklistedIP.getIpAddress());
    }
}
