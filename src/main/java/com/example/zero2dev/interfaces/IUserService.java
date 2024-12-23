package com.example.zero2dev.interfaces;

import com.example.zero2dev.dtos.LoginDTO;
import com.example.zero2dev.dtos.UpdateUserDTO;
import com.example.zero2dev.dtos.UserDTO;
import com.example.zero2dev.responses.UserResponse;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface IUserService {
    UserResponse createUser(UserDTO userDTO) throws MessagingException;
    UserResponse updateUser(UpdateUserDTO userDTO);
    UserResponse deleteUserById(Long id);
    List<UserResponse> getUserAvailable();
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getUserByUserName(String username);
    UserResponse getUserByPhoneNumber(String phoneNumber);
    UserResponse getUserByEmail(String email);
    List<UserResponse> getUnavailableUsers();
    List<UserResponse> getUserHighestScore();
    List<UserResponse> getUserByMatchEmail(String email);
    List<UserResponse> getUserByMatchPhoneNumber(String phoneNumber);
    List<UserResponse> getUserByMatchUserName(String username);
    Long totalAccepted(Long userId);
    UserResponse login(LoginDTO loginDTO, HttpServletRequest request);
}
