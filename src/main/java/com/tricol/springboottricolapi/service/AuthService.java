package com.tricol.springboottricolapi.service;

import com.tricol.springboottricolapi.dto.Request.LoginRequest;
import com.tricol.springboottricolapi.dto.Request.RegisterRequest;
import com.tricol.springboottricolapi.dto.Response.AuthResponse;
import com.tricol.springboottricolapi.entity.RefreshToken;
import com.tricol.springboottricolapi.entity.UserApp;
import com.tricol.springboottricolapi.exception.DuplicateRessourceException;
import com.tricol.springboottricolapi.repository.UserAppRepository;
import com.tricol.springboottricolapi.security.CustomUserDetailsService;
import com.tricol.springboottricolapi.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAppRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuditService auditService;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        String accessToken = tokenProvider.generateToken(authentication);
        UserApp user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        auditService.logAction(user, "LOGIN", "AUTH", null, "User logged in");

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .build();
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateRessourceException("User", "username", request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateRessourceException("User", "email", request.getEmail());
        }

        UserApp user = UserApp.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .build();

        userRepository.save(user);
        auditService.logAction(user, "REGISTER", "AUTH", null, "User registered");
    }

    @Transactional
    public AuthResponse refreshToken(String refreshTokenStr) {
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshTokenStr);
        refreshTokenService.verifyExpiration(refreshToken);

        UserApp user = refreshToken.getUser();
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String newAccessToken = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationMs() / 1000)
                .build();
    }


    @Transactional
    public void logout(String username) {
        UserApp user = userRepository.findByUsername(username).orElseThrow();
        refreshTokenService.deleteByUser(user);
        auditService.logAction(user, "LOGOUT", "AUTH", null, "User logged out");
    }
}
