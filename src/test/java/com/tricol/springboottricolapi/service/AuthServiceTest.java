package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.LoginRequest;
import com.tricol.springboottricolapi.dto.Request.RegisterRequest;
import com.tricol.springboottricolapi.dto.Response.AuthResponse;
import com.tricol.springboottricolapi.entity.RefreshToken;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.exception.DuplicateRessourceException;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserAppRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthService authService;

    private UserApp testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        testUser = UserApp.builder()
                .id(1L)
                .username("testuser")
                .email("test@tricol.ma")
                .password("encodedPassword")
                .enabled(true)
                .build();

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setUsername("newuser");
        registerRequest.setEmail("newuser@tricol.ma");
        registerRequest.setPassword("password123");
    }

    @Test
    void login_Success() {
        Authentication authentication = mock(Authentication.class);
        RefreshToken refreshToken = RefreshToken.builder()
                .token("refresh-token")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication)).thenReturn("access-token");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(refreshTokenService.createRefreshToken(testUser)).thenReturn(refreshToken);
        when(tokenProvider.getExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(auditService).logAction(any(), eq("LOGIN"), eq("AUTH"), any(), any());
    }

    @Test
    void register_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@tricol.ma")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserApp.class))).thenReturn(testUser);

        assertDoesNotThrow(() -> authService.register(registerRequest));

        verify(userRepository).save(any(UserApp.class));
        verify(auditService).logAction(any(), eq("REGISTER"), eq("AUTH"), any(), any());
    }

    @Test
    void register_DuplicateUsername_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(true);

        assertThrows(DuplicateRessourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_DuplicateEmail_ThrowsException() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@tricol.ma")).thenReturn(true);

        assertThrows(DuplicateRessourceException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void logout_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        assertDoesNotThrow(() -> authService.logout("testuser"));

        verify(refreshTokenService).deleteByUser(testUser);
        verify(auditService).logAction(any(), eq("LOGOUT"), eq("AUTH"), any(), any());
    }
}
