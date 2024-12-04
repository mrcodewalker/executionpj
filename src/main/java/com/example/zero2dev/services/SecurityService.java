package com.example.zero2dev.services;

import com.example.zero2dev.exceptions.ResourceNotFoundException;
import com.example.zero2dev.models.Role;
import com.example.zero2dev.models.User;
import com.example.zero2dev.storage.MESSAGE;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public static User getUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null) {
            return ((User) authentication.getPrincipal());
        }

        return null;
    }
    public static boolean checkValidUserId(Long userId){
        User user = getUserIdFromSecurityContext();
        if (user==null){
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
    public static void validateUserIdExcepAdmin(Long userId){
        if (!checkValidUserId(userId)){
            throw new ResourceNotFoundException(MESSAGE.GENERAL_ERROR);
        }
    }
}
