package com.example.zero2dev.services;

import com.example.zero2dev.dtos.UpdateUserDTO;
import com.example.zero2dev.dtos.UserDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.interfaces.IUserService;
import com.example.zero2dev.mapper.UserMapper;
import com.example.zero2dev.models.User;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.SubmissionRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.responses.UserResponse;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.SubmissionStatus;
import lombok.RequiredArgsConstructor;
import org.hibernate.sql.Update;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final String defaultAvatar = "";
    private final SubmissionRepository submissionRepository;
    @Override
    public UserResponse createUser(UserDTO userDTO) {
        this.validAccount(userDTO);
        String avatarUrl = (userDTO.getAvatarUrl() != null && !"null".equals(userDTO.getAvatarUrl()) && userDTO.getAvatarUrl().length() > 8)
                ? userDTO.getAvatarUrl()
                : this.defaultAvatar;
        User exchangeUser = this.mapper.toEntity(userDTO);
        String phoneNumber = userDTO.getPhoneNumber().length() > 8 ? userDTO.getPhoneNumber() : "";
        exchangeUser.setPhoneNumber(phoneNumber);
        exchangeUser.setAvatarUrl(avatarUrl);
        exchangeUser.setIsActive(true);
        exchangeUser.setTotalSolved(0L);
        return mapper.toResponse(this.userRepository.save(exchangeUser));
    }
    @Override
    public UserResponse updateUser(UpdateUserDTO updateUserDTO) {
        return mapper.toResponse(this.userRepository.save(this.exchangeEntity(updateUserDTO)));
    }

    @Override
    public UserResponse deleteUserById(Long id) {
        User user = this.collectUser(id);
        user.setIsActive(false);
        this.userRepository.save(user);
        return mapper.toResponse(user);
    }

    @Override
    public List<UserResponse> getUserAvailable() {
        return this.userRepository.findActiveUsers().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return this.userRepository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        return mapper.toResponse(this.collectUser(id));
    }

    @Override
    public UserResponse getUserByUserName(String username) {
        return mapper.toResponse(this.collectUserByUserName(username));
    }

    @Override
    public UserResponse getUserByPhoneNumber(String phoneNumber) {
        return mapper.toResponse(this.collectUserByPhoneNumber(phoneNumber));
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return mapper.toResponse(this.collectUserByEmail(email));
    }

    @Override
    public List<UserResponse> getUnavailableUsers() {
        return this.userRepository.findUnActiveUsers().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUserHighestScore() {
        return this.userRepository.findUsersWithHighestTotalSolved().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUserByMatchEmail(String email) {
        return Optional.ofNullable(this.userRepository.findMatchEmail(email))
                .filter(users -> !users.isEmpty())
                .map(users -> users.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find match email"));
    }

    @Override
    public List<UserResponse> getUserByMatchPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(this.userRepository.findMatchPhoneNumber(phoneNumber))
                .filter(phone -> !phone.isEmpty())
                .map(phone -> phone.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find match phoneNumber"));
    }

    @Override
    public List<UserResponse> getUserByMatchUserName(String username) {
        return Optional.ofNullable(this.userRepository.findMatchUsername(username))
                .filter(users -> !users.isEmpty())
                .map(users -> users.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException("Can not find match username"));
    }
    public void increaseSolved(Long userId){
        User user = this.getUser(userId);
        user.setTotalSolved(user.getTotalSolved()+1L);
        this.userRepository.save(user);
    }
    public void updateTotalSolved(Long userId, SubmissionStatus status){
        Long totalSolved = this.submissionRepository.countByUserIdAndStatus(userId, status);
        User user = this.getUser(userId);
        user.setTotalSolved(totalSolved);
        this.userRepository.save(user);
    }
    private User getUser(Long userId){
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    private User collectUser(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Can not find user"));
    }
    private boolean existByPhoneNumber(String phoneNumber){
        return phoneNumber != null && phoneNumber.length() > 8 && this.userRepository.existsByPhoneNumber(phoneNumber);
    }
    private User collectUserByUserName(String username){
        return this.userRepository.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("An error has been detected"));
    }
    private User collectUserByEmail(String email){
        return this.userRepository.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("An error has been detected"));
    }
    private User collectUserByPhoneNumber(String phoneNumber){
        return this.userRepository.getUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException("An error has been detected"));
    }
    private boolean existByEmail(String email){
        return email != null && email.length()>6 && this.userRepository.existsByEmail(email);
    }
    private boolean existByUserName(String username){
        return username != null && username.length()>6 && this.userRepository.existsByUsername(username);
    }
    private void validAccount(UserDTO userDTO){
        if (!userDTO.getEmail().contains("@gmail.com")){
            throw new ValueNotValidException("Can not accept this value right now");
        }
        if (this.existByEmail(userDTO.getEmail())){
            throw new ValueNotValidException("An email has been existed");
        }
        if (this.existByPhoneNumber(userDTO.getPhoneNumber())){
            throw new ValueNotValidException("A phone number has been existed");
        }
        if (this.existByUserName(userDTO.getUsername())) {
            throw new ValueNotValidException("An username has been existed");
        }
    }
    private User exchangeEntity(UpdateUserDTO updateUserDTO){
        User existingUser = this.getUserExisting(updateUserDTO.getId(), updateUserDTO.getUsername());
        if (updateUserDTO.getFullName()!=null){
            existingUser.setFullName(updateUserDTO.getFullName());
        }
        if (updateUserDTO.getAvatarUrl()!=null){
            existingUser.setAvatarUrl(updateUserDTO.getAvatarUrl());
        }
        if (updateUserDTO.getPhoneNumber()!=null){
            existingUser.setPhoneNumber(updateUserDTO.getPhoneNumber());
        }
        return existingUser;
    }
    private User getUserExisting(Long id, String username){
        return (id!=null && id>0)
                ? this.collectUser(id)
                : this.collectUserByUserName(username);
    }
}
