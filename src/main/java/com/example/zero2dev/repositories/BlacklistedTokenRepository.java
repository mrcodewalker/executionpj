package com.example.zero2dev.repositories;

import com.example.zero2dev.models.BlacklistedToken;
import com.example.zero2dev.storage.BlacklistReason;
import com.example.zero2dev.storage.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {
    Optional<BlacklistedToken> findByToken(String token);
    List<BlacklistedToken> findByUsername(String username);
    List<BlacklistedToken> findByTokenType(TokenType tokenType);
    List<BlacklistedToken> findByReason(BlacklistReason reason);

    boolean existsByToken(String token);
    void deleteByUsername(String username);
}
