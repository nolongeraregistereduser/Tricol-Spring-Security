package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.AssignRoleRequest;
import com.tricol.springboottricolapi.dto.Response.UserResponse;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.exception.ResourceNotFoundException;
import com.tricol.springboottricolapi.repository.PermissionRepository;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.repository.UserPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserManagementServiceTest {

    @Mock
    private UserAppRepository userRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    private PermissionService permissionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private UserManagementService userManagementService;

    private UserApp user;
    private UserApp admin;

    @BeforeEach
    void setUp() {
        user = UserApp.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(RoleApp.MAGASINIER)))
                .build();

        admin = UserApp.builder()
                .id(2L)
                .username("admin")
                .email("admin@test.com")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(RoleApp.ADMIN)))
                .build();
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(user));

        List<UserResponse> result = userManagementService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse result = userManagementService.getUserById(1L);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_WhenNotExists_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userManagementService.getUserById(1L));
    }

    @Test
    void assignRole_ShouldAddRoleToUser() {
        AssignRoleRequest request = new AssignRoleRequest();
        request.setRole(RoleApp.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        userManagementService.assignRole(1L, request, "admin");

        assertTrue(user.getRoles().contains(RoleApp.ADMIN));
        verify(userRepository).save(user);
        verify(auditService).logAction(any(), eq("ASSIGN_ROLE"), any(), any(), any());
    }

    @Test
    void removeRole_ShouldRemoveRoleFromUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        userManagementService.removeRole(1L, RoleApp.MAGASINIER, "admin");

        assertFalse(user.getRoles().contains(RoleApp.MAGASINIER));
        verify(userRepository).save(user);
    }

    @Test
    void toggleUserStatus_ShouldChangeEnabledStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(admin));

        boolean initialStatus = user.getEnabled();
        userManagementService.toggleUserStatus(1L, "admin");

        assertEquals(!initialStatus, user.getEnabled());
        verify(userRepository).save(user);
    }

    @Test
    void getUserPermissions_ShouldReturnPermissions() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(permissionService.getUserPermissions(user)).thenReturn(Set.of("READ_PRODUCT"));
        when(userPermissionRepository.findByUser(user)).thenReturn(Collections.emptyList());

        var result = userManagementService.getUserPermissions(1L);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
