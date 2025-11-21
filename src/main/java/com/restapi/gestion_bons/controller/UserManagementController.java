package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.dto.user.*;
import com.restapi.gestion_bons.service.security.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('USER_MANAGE')")
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.getUserById(userId));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<UserResponseDTO> assignRole(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRoleRequest request) {
        return ResponseEntity.ok(userManagementService.assignRole(userId, request.getRoleId()));
    }

    @PutMapping("/{userId}/permissions")
    public ResponseEntity<UserResponseDTO> customizePermission(
            @PathVariable Long userId,
            @Valid @RequestBody CustomizePermissionRequest request) {
        return ResponseEntity.ok(userManagementService.customizePermission(
                userId, 
                request.getPermissionId(), 
                request.getGranted()));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponseDTO>> getAllRoles() {
        return ResponseEntity.ok(userManagementService.getAllRoles());
    }

    @GetMapping("/permissions")
    public ResponseEntity<List<PermissionResponseDTO>> getAllPermissions() {
        return ResponseEntity.ok(userManagementService.getAllPermissions());
    }
}
