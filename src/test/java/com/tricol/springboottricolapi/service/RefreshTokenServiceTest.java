package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.entity.RefreshToken;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.exception.ResourceNotFoundException;
import com.tricol.springboottricolapi.repository.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private UserApp user;
    private RefreshToken refreshToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(refreshTokenService, "refreshTokenDurationMs", 604800000L);

        user = UserApp.builder()
                .id(1L)
                .username("testuser")
                .email("test@test.com")
                .build();

        refreshToken = RefreshToken.builder()
                .id(1L)
                .token("test-token")
                .user(user)
                .expiryDate(Instant.now().plusMillis(604800000L))
                .revoked(false)
                .build();
    }

    @Test
    void createRefreshToken_ShouldCreateToken() {
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        RefreshToken result = refreshTokenService.createRefreshToken(user);

        assertNotNull(result);
        verify(refreshTokenRepository).deleteByUser(user);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void findByToken_WhenExists_ShouldReturnToken() {
        when(refreshTokenRepository.findByToken("test-token")).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.findByToken("test-token");

        assertNotNull(result);
        assertEquals("test-token", result.getToken());
    }

    @Test
    void findByToken_WhenNotExists_ShouldThrowException() {
        when(refreshTokenRepository.findByToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> refreshTokenService.findByToken("invalid-token"));
    }

    @Test
    void verifyExpiration_WhenValid_ShouldReturnToken() {
        RefreshToken result = refreshTokenService.verifyExpiration(refreshToken);

        assertNotNull(result);
        verify(refreshTokenRepository, never()).delete(any());
    }

    @Test
    void verifyExpiration_WhenExpired_ShouldThrowException() {
        refreshToken.setExpiryDate(Instant.now().minusMillis(1000));

        assertThrows(RuntimeException.class, () -> refreshTokenService.verifyExpiration(refreshToken));
        verify(refreshTokenRepository).delete(refreshToken);
    }

    @Test
    void deleteByUser_ShouldDeleteTokens() {
        refreshTokenService.deleteByUser(user);

        verify(refreshTokenRepository).deleteByUser(user);
    }
}
