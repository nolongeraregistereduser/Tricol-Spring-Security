package com.tricol.springboottricolapi.controller;

import com.tricol.springboottricolapi.dto.Request.AssignRoleRequest;
import com.tricol.springboottricolapi.dto.Request.UpdatePermissionRequest;
import com.tricol.springboottricolapi.dto.Response.PermissionResponse;
import com.tricol.springboottricolapi.dto.Response.UserResponse;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.service.UserManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.getUserById(userId));
    }

    @PostMapping("/{userId}/roles")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> assignRole(
            @PathVariable Long userId,
            @Valid @RequestBody AssignRoleRequest request,
            Authentication authentication) {
        userManagementService.assignRole(userId, request, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}/roles/{role}")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> removeRole(
            @PathVariable Long userId,
            @PathVariable RoleApp role,
            Authentication authentication) {
        userManagementService.removeRole(userId, role, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_PERMISSIONS')")
    public ResponseEntity<Void> updatePermission(
            @PathVariable Long userId,
            @Valid @RequestBody UpdatePermissionRequest request,
            Authentication authentication) {
        userManagementService.updatePermission(userId, request, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/permissions")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<List<PermissionResponse>> getUserPermissions(@PathVariable Long userId) {
        return ResponseEntity.ok(userManagementService.getUserPermissions(userId));
    }

    @PatchMapping("/{userId}/toggle-status")
    @PreAuthorize("hasAuthority('MANAGE_USERS')")
    public ResponseEntity<Void> toggleUserStatus(
            @PathVariable Long userId,
            Authentication authentication) {
        userManagementService.toggleUserStatus(userId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}
