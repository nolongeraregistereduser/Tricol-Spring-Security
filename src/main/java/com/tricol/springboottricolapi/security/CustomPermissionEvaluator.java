package com.tricol.springboottricolapi.security;

import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.service.PermissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("permissionEvaluator")
@RequiredArgsConstructor
public class CustomPermissionEvaluator {

    private final PermissionService permissionService;
    private final UserAppRepository userRepository;

    public boolean hasPermission(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();
        UserApp user = userRepository.findByUsername(username).orElse(null);
        
        if (user == null) {
            return false;
        }

        return permissionService.hasPermission(user, permission);
    }
}
