package com.example.zero2dev.services;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.exceptions.ValueNotValidException;
import com.example.zero2dev.filter.JwtTokenProvider;
import com.example.zero2dev.models.Role;
import com.example.zero2dev.models.User;
import com.example.zero2dev.storage.MESSAGE;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final JwtTokenProvider jwtTokenProvider;

    public static User getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return ((User) authentication.getPrincipal());
        }

        return null;
    }
    public static Long getUserIdByToken(){
        User user = getUserIdFromSecurityContext();
        if (user==null){
            throw new ValueNotValidException(MESSAGE.VALUE_NOT_FOUND_EXCEPTION);
        }
        return user.getId();
    }
    public static boolean checkValidUserId(Long userId){
        User user = getUserIdFromSecurityContext();
        if (user==null||userId<=0){
            return false;
        }
        if (user.getRole().getRoleName().equals(Role.ADMIN)
                || user.getRole().getId().equals(1L)){
            return true;
        }
        if (!user.getId().equals(userId)) {
            return false;
        }
        return user.getIsActive();
    }
    public static void validateUserIdExceptAdmin(Long userId){
        if (!checkValidUserId(userId)){
            throw new ResourceNotFoundException(MESSAGE.FORBIDDEN_REQUEST);
        }
    }
    public final String getSessionId(){
        return this.jwtTokenProvider.getSessionIdFromToken();
    }
}
