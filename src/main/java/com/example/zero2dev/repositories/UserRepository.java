package com.example.zero2dev.repositories;

import com.example.zero2dev.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.isActive = true")
    Optional<User> getUserByUsername(String username);
    Optional<User> getUserByEmail(String email);
    Optional<User> getUserByPassword(String password);
    Optional<User> getUserByForgot(String forgot);
    Optional<User> getUserByPhoneNumber(String phoneNumber);
    @Query("SELECT u FROM User u WHERE u.isActive = true")
    List<User> findActiveUsers();
    @Query("SELECT u FROM User u WHERE u.isActive = false")
    List<User> findUnActiveUsers();
    @Query(value = "SELECT u.* FROM user u WHERE u.total_solved = (SELECT MAX(us.total_solved) FROM user us)", nativeQuery = true)
    List<User> findUsersWithHighestTotalSolved();
    @Query("SELECT u FROM User u WHERE u.email LIKE %:email%")
    List<User> findMatchEmail(@Param("email") String email);
    @Query("SELECT u FROM User u WHERE u.phoneNumber LIKE %:phoneNumber%")
    List<User> findMatchPhoneNumber(@Param("phoneNumber") String phoneNumber);
    @Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
    List<User> findMatchUsername(@Param("username") String username);
    @Query("SELECT " +
            "CASE WHEN COUNT(CASE WHEN u.email LIKE %:email% THEN 1 END) > 0 THEN true ELSE false END AS emailExists, " +
            "CASE WHEN COUNT(CASE WHEN u.username = :username THEN 1 END) > 0 THEN true ELSE false END AS usernameExists " +
            "FROM User u")
    Map<String, Boolean> checkUserExistence(
            @Param("email") String email,
            @Param("username") String username
    );
}
