package com.example.zero2dev.repositories;

import com.example.zero2dev.models.Token;
import com.example.zero2dev.models.User;
import com.example.zero2dev.storage.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.expiredAt > CURRENT_TIMESTAMP")
    List<Token> findAllValidTokensByUser(@Param("userId") Long userId);

    Optional<Token> findByToken(String token);
    Optional<Token> findByUserAndTokenType(User user, TokenType tokenType);
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId")
    Optional<Token> findByUserId(@Param("userId") Long userId);
}
