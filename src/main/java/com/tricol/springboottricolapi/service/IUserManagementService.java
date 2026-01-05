package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.AssignRoleRequest;
import com.tricol.springboottricolapi.dto.Request.UpdatePermissionRequest;
import com.tricol.springboottricolapi.dto.Response.PermissionResponse;
import com.tricol.springboottricolapi.dto.Response.UserResponse;
import com.tricol.springboottricolapi.entity.enums.RoleApp;

import java.util.List;

public interface IUserManagementService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long userId);
    void assignRole(Long userId, AssignRoleRequest request, String adminUsername);
    void removeRole(Long userId, RoleApp role, String adminUsername);
    void updatePermission(Long userId, UpdatePermissionRequest request, String adminUsername);
    List<PermissionResponse> getUserPermissions(Long userId);
    void toggleUserStatus(Long userId, String adminUsername);
}
