package com.example.zero2dev.services;

import com.example.zero2dev.dtos.LoginDTO;
import com.example.zero2dev.dtos.UpdateUserDTO;
import com.example.zero2dev.dtos.UserDTO;
import com.example.zero2dev.dtos.UserSessionDTO;
import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.filter.JwtTokenProvider;
import com.example.zero2dev.interfaces.IUserService;
import com.example.zero2dev.mapper.UserMapper;
import com.example.zero2dev.models.BlacklistedIP;
import com.example.zero2dev.models.Role;
import com.example.zero2dev.models.User;
import com.example.zero2dev.models.UserSession;
import com.example.zero2dev.repositories.ProblemRepository;
import com.example.zero2dev.repositories.SubmissionRepository;
import com.example.zero2dev.repositories.UserRepository;
import com.example.zero2dev.responses.ApiResponse;
import com.example.zero2dev.responses.AuthenticationResponse;
import com.example.zero2dev.responses.UserResponse;
import com.example.zero2dev.storage.LoginStatus;
import com.example.zero2dev.storage.MESSAGE;
import com.example.zero2dev.storage.SubmissionStatus;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.sql.Update;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;
    private final String defaultAvatar = "";
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SubmissionRepository submissionRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleService roleService;
    private final AuthenticationService authenticationService;
    private final LoginAttemptService loginAttemptService;
    private final IPSecurityService ipSecurityService;
    private final UserSessionService userSessionService;
    private final EmailService emailService;
    public void verifyEmail(String token, String username){
        User user = this.userRepository.getUserByForgot(token)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.TOKEN_EXPIRED));
        if (!user.getUsername().equalsIgnoreCase(username)){
            throw new ResourceNotFoundException(MESSAGE.INPUT_NOT_MATCH_EXCEPTION);
        }
        user.setIsActive(true);
        this.userRepository.save(user);
    }
    public ResponseEntity<?> forgotPassword(String email){
        User user = this.collectUserByEmail(email);
        if (user == null) {
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        String resetToken = UUID.randomUUID().toString();
        this.savePasswordResetToken(user, resetToken);

        String resetUrl = "http://localhost:4200/reset-password?token=" + resetToken;

        try {
            emailService.sendResetPassword(email, resetUrl);
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(new ApiResponse(false, "Failed to send email. Please try again later."));
        }

        return ResponseEntity.ok(new ApiResponse(true, "Password reset email has been sent."));
    }
    public void resetPassword(String token, String newPassword){
        User user = this.userRepository.getUserByForgot(token)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.TOKEN_EXPIRED));
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setForgot("RESET_PASSWORD_SUCCESSFULLY");
        this.userRepository.save(user);
    }
    private void savePasswordResetToken(User user, String resetToken){
        user.setForgot(resetToken);
        this.userRepository.save(user);
    }
    @Override
    public UserResponse createUser(UserDTO userDTO) throws MessagingException {
        this.validAccount(userDTO);
        String avatarUrl = (userDTO.getAvatarUrl() != null && !"null".equals(userDTO.getAvatarUrl()) && userDTO.getAvatarUrl().length() > 8)
                ? userDTO.getAvatarUrl()
                : this.defaultAvatar;
        User exchangeUser = this.mapper.toEntity(userDTO);
        exchangeUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        String phoneNumber = userDTO.getPhoneNumber().length() > 8 ? userDTO.getPhoneNumber() : "";
        exchangeUser.setPhoneNumber(phoneNumber);
        exchangeUser.setRole(this.roleService.getRoleNew(2L));
        exchangeUser.setAvatarUrl(avatarUrl);
        exchangeUser.setIsActive(false);
        String token = UUID.randomUUID().toString();
        exchangeUser.setForgot(token);
        String verificationUrl = "http://localhost:8081/api/v1/user/verify?token="+token+"&username="+exchangeUser.getUsername();
        try {
            emailService.sendVerificationEmail(exchangeUser.getEmail(), verificationUrl);
        } catch (MessagingException e) {
            throw new ResourceNotFoundException(MESSAGE.GENERAL_ERROR);
        }
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
        SecurityService.validateUserIdExceptAdmin(id);
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
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<UserResponse> getUserByMatchPhoneNumber(String phoneNumber) {
        return Optional.ofNullable(this.userRepository.findMatchPhoneNumber(phoneNumber))
                .filter(phone -> !phone.isEmpty())
                .map(phone -> phone.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public List<UserResponse> getUserByMatchUserName(String username) {
        return Optional.ofNullable(this.userRepository.findMatchUsername(username))
                .filter(users -> !users.isEmpty())
                .map(users -> users.stream()
                        .map(mapper::toResponse)
                        .collect(Collectors.toList()))
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    @Override
    public Long totalAccepted(Long userId) {
        return this.submissionRepository.countByUserIdAndStatus(userId, SubmissionStatus.ACCEPTED);
    }

    @Override
    public UserResponse login(LoginDTO loginDTO, HttpServletRequest request) {
        loginDTO.setIpAddress(IpService.getClientIp(request));
        User user = (EmailValidator.getInstance().isValid(loginDTO.getUsername())) ?
                this.collectUserByEmail(loginDTO.getUsername()) :
                this.collectUserByUserName(loginDTO.getUsername());
        if (user == null) {
            throw new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        loginDTO.setUsername(user.getUsername());
        if (loginAttemptService.isAccountLocked(loginDTO.getUsername(), loginDTO.getIpAddress())) {
            this.ipSecurityService.createIPBlackList(request);
            throw new ValueNotValidException(MESSAGE.IP_BLACKLISTED);
        }
        if (!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())){
            this.loginAttemptService.recordLoginAttempt(loginDTO, LoginStatus.FAILED, request);
            throw new ValueNotValidException(MESSAGE.INPUT_NOT_MATCH_EXCEPTION);
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), loginDTO.getPassword(),
                user.getAuthorities()
        );
        System.out.println("HAI DEP TRAI"+authenticationToken);
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (Exception e) {
            System.out.println("HAI DEP TRAI");
            this.loginAttemptService.recordLoginAttempt(loginDTO, LoginStatus.FAILED, request);
            throw new ValueNotValidException(e.getMessage());
        }
        System.out.println("HAI DEP TRAI");
        UserSessionDTO userSessionDTO = UserSessionDTO.builder()
                .userId(user.getId())
                .deviceInfo(IpService.generateDeviceInfoString(request))
                .ipAddress(loginDTO.getIpAddress())
                .isActive(true)
                .build();
        System.out.println("HAI DEP TRAI");
        UserSession session = userSessionService.createSession(userSessionDTO, user);
        System.out.println("HAI DEP TRAI");
        UserResponse response = this.mapper.toResponse(user);
        response.setAuthenticationResponse(authenticationService.login(session, user));
        response.setRole(user.getRole().getRoleName());
        loginDTO.setUsername(user.getUsername());
        response.setId(24112004+user.getId());
        this.loginAttemptService.recordLoginAttempt(loginDTO, LoginStatus.SUCCESS, request);
        return response;
    }

    private User getUser(Long userId){
        return this.userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }

    private User collectUser(Long id){
        return this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION));
    }
    private boolean existByPhoneNumber(String phoneNumber){
        return phoneNumber != null && phoneNumber.length() > 8 && this.userRepository.existsByPhoneNumber(phoneNumber);
    }
    private User collectUserByUserName(String username){
        return this.userRepository.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.LOCKED_ACCOUNT));
    }
    private User collectUserByEmail(String email){
        return this.userRepository.getUserByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.GENERAL_ERROR));
    }
    private User collectUserByPhoneNumber(String phoneNumber){
        return this.userRepository.getUserByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new ResourceNotFoundException(MESSAGE.GENERAL_ERROR));
    }
    private boolean existByEmail(String email){
        return email != null && email.length()>6 && this.userRepository.existsByEmail(email);
    }
    private boolean existByUserName(String username){
        return username != null && username.length()>6 && this.userRepository.existsByUsername(username);
    }
    private void validAccount(UserDTO userDTO){
        if (userDTO.getPassword().length()<8){
            throw new ValueNotValidException(MESSAGE.INPUT_SIZE_ERROR);
        }
        if (userDTO.getUsername().length()<8){
            throw new ValueNotValidException(MESSAGE.INPUT_SIZE_ERROR);
        }
        if (!userDTO.getEmail().contains("@gmail.com")){
            throw new ValueNotValidException(MESSAGE.INPUT_NOT_MATCH_EXCEPTION);
        }
        Map<String, Boolean> checkResult = userRepository.checkUserExistence(
                userDTO.getEmail(),
                userDTO.getUsername());
        if (checkResult.get("usernameExists")){
            throw new ValueNotValidException(MESSAGE.EXISTED_USERNAME);
        }
        if (checkResult.get("emailExists")) {
            throw new ValueNotValidException(MESSAGE.EXISTED_EMAIL);
        }
    }
    private User exchangeEntity(UpdateUserDTO updateUserDTO){
        SecurityService.validateUserIdExceptAdmin(updateUserDTO.getId());
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
    public final boolean isIpAddressUnban(String ipAddress){
        return ipSecurityService.isIPUnban(ipAddress);
    }
}
