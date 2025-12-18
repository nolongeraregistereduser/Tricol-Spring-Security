package com.restapi.gestion_bons.service.security;

import com.restapi.gestion_bons.dao.UserDAO;
import com.restapi.gestion_bons.entitie.Permission;
import com.restapi.gestion_bons.entitie.UserApp;
import com.restapi.gestion_bons.entitie.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = userDAO.findByUsernameWithRoleAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.getAccountNonLocked())
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }

    private Set<GrantedAuthority> getAuthorities(UserApp user) {
        Set<String> permissions = new HashSet<>();

        if (user.getRole() != null && user.getRole().getDefaultPermissions() != null) {
            Set<String> rolePermissions = user.getRole().getDefaultPermissions()
                    .stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            permissions.addAll(rolePermissions);
        }

        if (user.getCustomPermissions() != null) {
            for (UserPermission userPermission : user.getCustomPermissions()) {
                String permissionName = userPermission.getPermission().getName();
                if (userPermission.getGranted()) {
                    permissions.add(permissionName);
                } else {
                    permissions.remove(permissionName);
                }
            }
        }

        if (user.getRole() != null) {
            permissions.add("ROLE_" + user.getRole().getName().name());
        }

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
