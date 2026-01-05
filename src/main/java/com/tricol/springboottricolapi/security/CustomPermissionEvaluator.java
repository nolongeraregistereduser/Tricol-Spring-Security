package com.tricol.springboottricolapi.security;

import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.service.PermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component("permissionEvaluator")
@RequiredArgsConstructor
public class CustomPermissionEvaluator {

    private final PermissionService permissionService;
    private final UserAppRepository userRepository;

    public boolean hasPermission(Authentication authentication, String permission) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication is null or not authenticated");
            return false;
        }

        log.info("Checking permission: {} for user: {}", permission, authentication.getName());
        log.info("Authentication type: {}", authentication.getClass().getName());

        if (authentication instanceof JwtAuthenticationToken) {
            return hasKeycloakPermission((JwtAuthenticationToken) authentication, permission);
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            UserApp user = userRepository.findByUsername(username).orElse(null);

            if (user == null) {
                log.warn("User not found: {}", username);
                return false;
            }

            return permissionService.hasPermission(user, permission);
        }

        log.warn("Unsupported authentication type: {}", authentication.getClass().getName());
        return false;
    }



    private boolean hasKeycloakPermission(JwtAuthenticationToken jwtAuth, String permission) {
    Collection<? extends GrantedAuthority> authorities = jwtAuth.getAuthorities();
    log.info("Keycloak authorities: {}", authorities);

    Set<RoleApp> roles = new HashSet<>();
    for (GrantedAuthority authority : authorities) {
        String role = authority.getAuthority();
        log.info("Checking role: {}", role);
        
        try {
            RoleApp roleApp = RoleApp.valueOf(role);
            roles.add(roleApp);
        } catch (IllegalArgumentException e) {
            log.warn("Role {} not found in RoleApp enum", role);
        }
    }

    boolean hasPermission = permissionService.getUserPermissionsByRoles(roles).contains(permission);
    
    log.info("Permission {} {} for Keycloak user", permission, hasPermission ? "GRANTED" : "DENIED");
    return hasPermission;
}

}
