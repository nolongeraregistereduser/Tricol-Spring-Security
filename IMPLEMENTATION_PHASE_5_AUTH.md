# 🚀 PHASE 5: SECURITY CONFIGURATION & AUTHENTICATION

## MINI-PHASE 5.1: Update SecurityConfig (15 minutes)

**What:** Configure Spring Security to use JWT

**Action:**
1. Open file: `src/main/java/com/restapi/gestion_bons/config/SecurityConfig.java`
2. Replace ALL content with this code:

```java
package com.restapi.gestion_bons.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/ping", "/actuator/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 5.2: Create Auth DTOs (15 minutes)

**What:** Create request/response objects for authentication

**Action:**
1. Create folder: `src/main/java/com/restapi/gestion_bons/dto/auth/`
2. Create these 5 files:

**RegisterRequest.java:**
```java
package com.restapi.gestion_bons.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6)
    private String password;
}
```

**LoginRequest.java:**
```java
package com.restapi.gestion_bons.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {
    
    @NotBlank(message = "Username is required")
    private String username;
    
    @NotBlank(message = "Password is required")
    private String password;
}
```

**AuthResponse.java:**
```java
package com.restapi.gestion_bons.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String username;
    private String email;
    private String role;
}
```

**RefreshTokenRequest.java:**
```java
package com.restapi.gestion_bons.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequest {
    
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
```

**MessageResponse.java:**
```java
package com.restapi.gestion_bons.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String message;
}
```

3. Save all files
4. ✅ Verify: All compile successfully

---

## MINI-PHASE 5.3: Create RefreshTokenDAO (10 minutes)

**What:** Repository for refresh tokens

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/dao/RefreshTokenDAO.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.RefreshToken;
import com.restapi.gestion_bons.entitie.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenDAO extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.user = :user")
    void deleteByUser(UserApp user);
    
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.token = :token")
    void revokeToken(String token);
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 5.4: Create RefreshTokenService (15 minutes)

**What:** Service to manage refresh tokens

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/service/security/RefreshTokenService.java`
2. Copy this code:

```java
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
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 5.5: Create AuthService (20 minutes)

**What:** Service with authentication business logic

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/service/security/AuthService.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.service.security;

import com.restapi.gestion_bons.dao.UserDAO;
import com.restapi.gestion_bons.dto.auth.*;
import com.restapi.gestion_bons.entitie.RefreshToken;
import com.restapi.gestion_bons.entitie.UserApp;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserDAO userDAO;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userDAO.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        UserApp user = UserApp.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .enabled(true)
                .accountNonLocked(true)
                .build();

        userDAO.save(user);

        return MessageResponse.builder()
                .message("User registered successfully. Please contact admin to assign a role.")
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserApp user = userDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        String accessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole() != null ? user.getRole().getName().name() : null)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                    String accessToken = jwtService.generateAccessToken(userDetails);

                    return AuthResponse.builder()
                            .accessToken(accessToken)
                            .refreshToken(requestRefreshToken)
                            .tokenType("Bearer")
                            .userId(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .role(user.getRole() != null ? user.getRole().getName().name() : null)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    @Transactional
    public MessageResponse logout(RefreshTokenRequest request) {
        refreshTokenService.revokeToken(request.getRefreshToken());
        return MessageResponse.builder()
                .message("Logged out successfully")
                .build();
    }
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 5.6: Create AuthController (15 minutes)

**What:** REST endpoints for authentication

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/controller/AuthController.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.controller;

import com.restapi.gestion_bons.dto.auth.*;
import com.restapi.gestion_bons.service.security.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<MessageResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.logout(request));
    }
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 5.7: Test Authentication (20 minutes)

**What:** Test login and protected endpoints

**Action:**
1. Compile: `mvn clean compile`
2. Run: `mvn spring-boot:run`
3. Wait for application to start
4. Test login using curl or Postman:

**Test 1: Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\":\"admin\",\"password\":\"password123\"}"
```

Expected response:
```json
{
  "accessToken": "eyJhbGc...",
  "refreshToken": "uuid-string",
  "tokenType": "Bearer",
  "userId": 1,
  "username": "admin",
  "email": "admin@tricol.com",
  "role": "ADMIN"
}
```

**Test 2: Access Protected Endpoint**
```bash
curl -X GET http://localhost:8080/api/v1/produits \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN_HERE"
```

Should return products (if you have data).

**Test 3: Access Without Token**
```bash
curl -X GET http://localhost:8080/api/v1/produits
```

Should return 403 Forbidden.

5. ✅ Verify: All tests pass

---

## 🎉 PHASE 5 CHECKPOINT

**What you've completed:**
- ✅ Spring Security configured
- ✅ Auth DTOs created
- ✅ RefreshTokenService created
- ✅ AuthService created
- ✅ AuthController created
- ✅ Authentication working!

**You can now:**
- Register users
- Login and get JWT tokens
- Access protected endpoints with token
- Refresh tokens
- Logout

**Next:** Add permission-based authorization

---
