package com.example.zero2dev.repositories;

import com.example.zero2dev.models.LoginAttempt;
import com.example.zero2dev.storage.LoginStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    List<LoginAttempt> findByUsernameAndIpAddressAndStatusAndAttemptTimeAfter(
            String username,
            String ipAddress,
            LoginStatus status,
            LocalDateTime timeThreshold
    );
    long countByIpAddressAndStatusAndAttemptTimeAfter(
      String ipAddress,
      LoginStatus status,
      LocalDateTime timeThreshSold
    );
    @Modifying
    @Query(value = "ALTER TABLE login_attempt AUTO_INCREMENT = 1", nativeQuery = true)
    void resetAutoIncrement();
}
