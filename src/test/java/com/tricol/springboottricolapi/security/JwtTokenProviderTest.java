package com.tricol.springboottricolapi.security;

import com.tricol.springboottricolapi.entity.UserApp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtSecret",
                "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpirationMs", 900000L);

        UserApp user = UserApp.builder()
                .username("testuser")
                .email("test@tricol.ma")
                .enabled(true)
                .build();

        UserDetailsImpl userDetails = UserDetailsImpl.build(user, Collections.emptySet());
        authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void generateToken_Success() {
        String token = jwtTokenProvider.generateToken(authentication);

        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        String token = jwtTokenProvider.generateToken(authentication);

        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        String invalidToken = "invalid.token.here";

        assertFalse(jwtTokenProvider.validateToken(invalidToken));
    }

    @Test
    void getUsernameFromToken_Success() {
        String token = jwtTokenProvider.generateToken(authentication);

        String username = jwtTokenProvider.getUsernameFromToken(token);

        assertEquals("testuser", username);
    }

    @Test
    void getExpirationMs_ReturnsCorrectValue() {
        assertEquals(900000L, jwtTokenProvider.getExpirationMs());
    }
}
