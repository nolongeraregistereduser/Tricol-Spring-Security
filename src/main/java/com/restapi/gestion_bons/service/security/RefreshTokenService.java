package com.restapi.gestion_bons.service.security;

import com.restapi.gestion_bons.dao.RefreshTokenDAO;
import com.restapi.gestion_bons.dao.UserDAO;
import com.restapi.gestion_bons.entitie.RefreshToken;
import com.restapi.gestion_bons.entitie.UserApp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenDAO refreshTokenDAO;
    private final UserDAO userDAO;

    public RefreshToken createRefreshToken(Long userId) {
        UserApp user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshTokenDurationMs))
                .revoked(false)
                .build();

        return refreshTokenDAO.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenDAO.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenDAO.delete(token);
            throw new RuntimeException("Refresh token expired. Please login again.");
        }
        return token;
    }

    @Transactional
    public void revokeToken(String token) {
        refreshTokenDAO.revokeToken(token);
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        UserApp user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenDAO.deleteByUser(user);
    }
}
