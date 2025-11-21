package com.restapi.gestion_bons.service.security;

import com.restapi.gestion_bons.dao.*;
import com.restapi.gestion_bons.dto.user.*;
import com.restapi.gestion_bons.entitie.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserDAO userDAO;
    private final RoleDAO roleDAO;
    private final PermissionDAO permissionDAO;
    private final UserPermissionDAO userPermissionDAO;

    public List<UserResponseDTO> getAllUsers() {
        return userDAO.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long userId) {
        UserApp user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponseDTO assignRole(Long userId, Long roleId) {
        UserApp user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        RoleApp role = roleDAO.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.setRole(role);
        userDAO.save(user);
        
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponseDTO customizePermission(Long userId, Long permissionId, Boolean granted) {
        UserApp user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Permission permission = permissionDAO.findById(permissionId)
                .orElseThrow(() -> new RuntimeException("Permission not found"));
        
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        
        UserPermission userPermission = userPermissionDAO
                .findByUserIdAndPermissionId(userId, permissionId)
                .orElse(UserPermission.builder()
                        .user(user)
                        .permission(permission)
                        .build());
        
        userPermission.setGranted(granted);
        userPermission.setModifiedBy(currentUsername);
        userPermissionDAO.save(userPermission);
        
        return mapToUserResponse(user);
    }

    public List<RoleResponseDTO> getAllRoles() {
        return roleDAO.findAll().stream()
                .map(this::mapToRoleResponse)
                .collect(Collectors.toList());
    }

    public List<PermissionResponseDTO> getAllPermissions() {
        return permissionDAO.findAll().stream()
                .map(this::mapToPermissionResponse)
                .collect(Collectors.toList());
    }

    private UserResponseDTO mapToUserResponse(UserApp user) {
        List<String> permissions = List.of();
        
        if (user.getRole() != null && user.getRole().getDefaultPermissions() != null) {
            permissions = user.getRole().getDefaultPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toList());
        }
        
        // Apply user-specific permission overrides
        List<UserPermission> userPermissions = userPermissionDAO.findByUserId(user.getId());
        for (UserPermission up : userPermissions) {
            String permName = up.getPermission().getName();
            if (up.getGranted()) {
                if (!permissions.contains(permName)) {
                    permissions.add(permName);
                }
            } else {
                permissions.remove(permName);
            }
        }
        
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .enabled(user.getEnabled())
                .accountNonLocked(user.getAccountNonLocked())
                .role(user.getRole() != null ? user.getRole().getName().name() : null)
                .createdAt(user.getCreatedAt())
                .permissions(permissions)
                .build();
    }

    private RoleResponseDTO mapToRoleResponse(RoleApp role) {
        List<String> permissions = role.getDefaultPermissions().stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
        
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName().name())
                .description(role.getDescription())
                .permissions(permissions)
                .build();
    }

    private PermissionResponseDTO mapToPermissionResponse(Permission permission) {
        return PermissionResponseDTO.builder()
                .id(permission.getId())
                .name(permission.getName())
                .description(permission.getDescription())
                .resource(permission.getResource())
                .action(permission.getAction())
                .build();
    }
}
