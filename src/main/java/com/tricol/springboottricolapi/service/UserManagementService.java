package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.AssignRoleRequest;
import com.tricol.springboottricolapi.dto.Request.UpdatePermissionRequest;
import com.tricol.springboottricolapi.dto.Response.PermissionResponse;
import com.tricol.springboottricolapi.dto.Response.UserResponse;
import com.tricol.springboottricolapi.entity.Permission;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.UserPermission;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.exception.ResourceNotFoundException;
import com.tricol.springboottricolapi.repository.PermissionRepository;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.repository.UserPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserManagementService {

    private final UserAppRepository userRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PermissionService permissionService;
    private final AuditService auditService;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long userId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        return mapToUserResponse(user);
    }

    @Transactional
    public void assignRole(Long userId, AssignRoleRequest request, String adminUsername) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.getRoles().add(request.getRole());
        userRepository.save(user);

        UserApp admin = userRepository.findByUsername(adminUsername).orElseThrow();
        auditService.logAction(admin, "ASSIGN_ROLE", "USER", userId.toString(),
                "Assigned role " + request.getRole() + " to user " + user.getUsername());
    }

    @Transactional
    public void removeRole(Long userId, RoleApp role, String adminUsername) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.getRoles().remove(role);
        userRepository.save(user);

        UserApp admin = userRepository.findByUsername(adminUsername).orElseThrow();
        auditService.logAction(admin, "REMOVE_ROLE", "USER", userId.toString(),
                "Removed role " + role + " from user " + user.getUsername());
    }

    @Transactional
    public void updatePermission(Long userId, UpdatePermissionRequest request, String adminUsername) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Permission permission = permissionRepository.findByName(request.getPermissionName())
                .orElseThrow(() -> new ResourceNotFoundException("Permission", "name", request.getPermissionName()));

        Optional<UserPermission> existingPermission = userPermissionRepository
                .findByUserAndPermission(user, permission);

        if (existingPermission.isPresent()) {
            UserPermission up = existingPermission.get();
            up.setGranted(request.getGranted());
            userPermissionRepository.save(up);
        } else {
            UserPermission newPermission = UserPermission.builder()
                    .user(user)
                    .permission(permission)
                    .granted(request.getGranted())
                    .build();
            userPermissionRepository.save(newPermission);
        }

        UserApp admin = userRepository.findByUsername(adminUsername).orElseThrow();
        auditService.logAction(admin, "UPDATE_PERMISSION", "USER", userId.toString(),
                "Updated permission " + request.getPermissionName() + " to " + request.getGranted());
    }

    public List<PermissionResponse> getUserPermissions(Long userId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        Set<String> rolePermissions = permissionService.getUserPermissions(user);

        List<PermissionResponse> responses = new ArrayList<>();

        for (String perm : rolePermissions) {
            responses.add(PermissionResponse.builder()
                    .name(perm)
                    .granted(true)
                    .source("ROLE")
                    .build());
        }

        List<UserPermission> customPermissions = userPermissionRepository.findByUser(user);
        for (UserPermission up : customPermissions) {
            responses.add(PermissionResponse.builder()
                    .name(up.getPermission().getName())
                    .granted(up.getGranted())
                    .source("CUSTOM")
                    .build());
        }

        return responses;
    }

    @Transactional
    public void toggleUserStatus(Long userId, String adminUsername) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        user.setEnabled(!user.getEnabled());
        userRepository.save(user);

        UserApp admin = userRepository.findByUsername(adminUsername).orElseThrow();
        auditService.logAction(admin, "TOGGLE_STATUS", "USER", userId.toString(),
                "User " + user.getUsername() + " status changed to " + user.getEnabled());
    }

    private UserResponse mapToUserResponse(UserApp user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(user.getRoles())
                .enabled(user.getEnabled())
                .build();
    }
}
