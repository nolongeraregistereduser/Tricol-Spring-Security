package com.tricol.springboottricolapi.security;

import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.entity.enums.RoleApp;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomPermissionEvaluatorTest {

    @Mock
    private PermissionService permissionService;

    @Mock
    private UserAppRepository userRepository;

    @InjectMocks
    private CustomPermissionEvaluator permissionEvaluator;

    private UserApp user;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        user = UserApp.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .enabled(true)
                .roles(new HashSet<>(Arrays.asList(RoleApp.ADMIN)))
                .build();

        UserDetailsImpl userDetails = UserDetailsImpl.build(user, Set.of("READ_PRODUCT", "CREATE_PRODUCT"));
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void hasPermission_WhenUserHasPermission_ShouldReturnTrue() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(permissionService.hasPermission(user, "READ_PRODUCT")).thenReturn(true);

        boolean result = permissionEvaluator.hasPermission(authentication, "READ_PRODUCT");

        assertTrue(result);
        verify(permissionService).hasPermission(user, "READ_PRODUCT");
    }

    @Test
    void hasPermission_WhenUserDoesNotHavePermission_ShouldReturnFalse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(permissionService.hasPermission(user, "DELETE_PRODUCT")).thenReturn(false);

        boolean result = permissionEvaluator.hasPermission(authentication, "DELETE_PRODUCT");

        assertFalse(result);
    }

    @Test
    void hasPermission_WhenAuthenticationIsNull_ShouldReturnFalse() {
        boolean result = permissionEvaluator.hasPermission(null, "READ_PRODUCT");

        assertFalse(result);
        verify(permissionService, never()).hasPermission(any(), any());
    }

    @Test
    void hasPermission_WhenUserNotFound_ShouldReturnFalse() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        boolean result = permissionEvaluator.hasPermission(authentication, "READ_PRODUCT");

        assertFalse(result);
    }
}
