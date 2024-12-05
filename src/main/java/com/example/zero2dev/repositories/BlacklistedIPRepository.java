package com.example.zero2dev.repositories;

import com.example.zero2dev.models.BlacklistedIP;
import com.example.zero2dev.storage.BlacklistStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BlacklistedIPRepository extends JpaRepository<BlacklistedIP, Long> {
    boolean existsByIpAddressAndStatusAndUnblacklistAtAfter(
            String ipAddress,
            BlacklistStatus status,
            LocalDateTime currentTime
    );
    Optional<BlacklistedIP> findByIpAddress(String ipAddress);
    List<BlacklistedIP> findByStatusAndUnblacklistAtBefore(
            BlacklistStatus status,
            LocalDateTime currentTime
    );
}
