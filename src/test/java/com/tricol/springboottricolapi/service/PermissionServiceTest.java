package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.Permission;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.UserPermission;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.repository.PermissionRepository;
import com.tricol.springboottricolapi.repository.UserPermissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private UserPermissionRepository userPermissionRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private UserApp testUser;

    @BeforeEach
    void setUp() {
        testUser = UserApp.builder()
                .id(1L)
                .username("testuser")
                .roles(Set.of(RoleApp.MAGASINIER))
                .build();
    }

    @Test
    void getUserPermissions_WithRoleOnly_ReturnsRolePermissions() {
        when(userPermissionRepository.findByUser(testUser)).thenReturn(List.of());

        Set<String> permissions = permissionService.getUserPermissions(testUser);

        assertFalse(permissions.isEmpty());
        assertTrue(permissions.contains("READ_STOCK"));
    }

    @Test
    void getUserPermissions_WithCustomPermission_ReturnsModifiedPermissions() {
        Permission permission = Permission.builder()
                .name("CREATE_EXIT_ORDER")
                .build();

        UserPermission userPermission = UserPermission.builder()
                .user(testUser)
                .permission(permission)
                .granted(false)
                .build();

        when(userPermissionRepository.findByUser(testUser)).thenReturn(List.of(userPermission));

        Set<String> permissions = permissionService.getUserPermissions(testUser);

        assertFalse(permissions.contains("CREATE_EXIT_ORDER"));
    }

    @Test
    void hasPermission_UserHasPermission_ReturnsTrue() {
        when(userPermissionRepository.findByUser(testUser)).thenReturn(List.of());

        boolean hasPermission = permissionService.hasPermission(testUser, "READ_STOCK");

        assertTrue(hasPermission);
    }

    @Test
    void hasPermission_UserDoesNotHavePermission_ReturnsFalse() {
        when(userPermissionRepository.findByUser(testUser)).thenReturn(List.of());

        boolean hasPermission = permissionService.hasPermission(testUser, "DELETE_SUPPLIER");

        assertFalse(hasPermission);
    }
}
