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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserAppRepository userRepository;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    private UserApp testUser;

    @BeforeEach
    void setUp() {
        testUser = UserApp.builder()
                .id(1L)
                .username("testuser")
                .email("test@tricol.ma")
                .password("password")
                .enabled(true)
                .roles(Set.of(RoleApp.MAGASINIER))
                .build();
    }

    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(permissionService.getUserPermissions(testUser)).thenReturn(Set.of("READ_STOCK"));

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.getUsername());
        assertTrue(userDetails.isEnabled());
    }

    @Test
    void loadUserByUsername_UserNotFound_ThrowsException() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, 
                () -> userDetailsService.loadUserByUsername("nonexistent"));
    }
}
