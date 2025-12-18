# 📋 STEPS 8-9: SECURITY CONFIGURATION & AUTHENTICATION

---

## ✅ STEP 8: CONFIGURE SPRING SECURITY

### 🎯 Goal
Configure Spring Security to use JWT authentication and define which endpoints are public vs protected.

### 📚 What You Need to Know

**SecurityFilterChain:**
Defines security rules for your application.

**Configuration Elements:**
1. **Password Encoder** - How to encrypt passwords
2. **Authentication Manager** - How to authenticate users
3. **HTTP Security** - Which endpoints require authentication
4. **Filter Chain** - Add our JWT filter
5. **CORS** - Allow cross-origin requests

### ✍️ Implementation

#### 8.1 Update SecurityConfig

**Location:** `src/main/java/com/restapi/gestion_bons/config/SecurityConfig.java`

Replace the existing content with:

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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

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
                // Disable CSRF (not needed for JWT)
                .csrf(AbstractHttpConfigurer::disable)
                
                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                
                // Configure authorization
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints (no authentication required)
                        .requestMatchers(
                                "/api/auth/**",           // Authentication endpoints
                                "/api/ping",              // Health check
                                "/actuator/**"            // Actuator endpoints
                        ).permitAll()
                        
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                
                // Stateless session (no server-side sessions)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                
                // Set authentication provider
                .authenticationProvider(authenticationProvider())
                
                // Add JWT filter before UsernamePasswordAuthenticationFilter
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

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

**Explanation:**

**1. @EnableMethodSecurity**
- Enables method-level security annotations
- Allows using @PreAuthorize on controller methods

**2. SecurityFilterChain**
Main security configuration:

**a) CSRF Disabled**
```java
.csrf(AbstractHttpConfigurer::disable)
```
- CSRF protection not needed for JWT
- JWT tokens are immune to CSRF attacks

**b) CORS Configuration**
```java
.cors(cors -> cors.configurationSource(corsConfigurationSource()))
```
- Allows frontend (React/Angular) to call API
- Configured for localhost:3000 and localhost:4200

**c) Authorization Rules**
```java
.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/auth/**").permitAll()  // Public
    .anyRequest().authenticated()                  // Protected
)
```
- `/api/auth/**` - Public (login, register)
- Everything else - Requires authentication

**d) Stateless Sessions**
```java
.sessionManagement(session -> session
    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
)
```
- No server-side sessions
- Each request must include JWT
- Scalable (no session storage)

**e) Add JWT Filter**
```java
.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
```
- Our JWT filter runs first
- Extracts and validates JWT
- Sets authentication before other filters

**3. PasswordEncoder Bean**
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}
```
- BCrypt is industry standard
- Automatically salts passwords
- Slow by design (prevents brute force)

**Example:**
```
Plain: "password123"
BCrypt: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"
```

**4. AuthenticationProvider Bean**
```java
@Bean
public AuthenticationProvider authenticationProvider() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(userDetailsService);
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
}
```
- Connects UserDetailsService and PasswordEncoder
- Used to authenticate username/password

**5. AuthenticationManager Bean**
```java
@Bean
public AuthenticationManager authenticationManager(AuthenticationConfiguration config) {
    return config.getAuthenticationManager();
}
```
- Needed for login endpoint
- Authenticates user credentials

**6. CORS Configuration**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(List.of("http://localhost:3000"));
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setAllowCredentials(true);
    return source;
}
```
- Allows frontend to call API
- Allows all HTTP methods
- Allows credentials (cookies, auth headers)

---

## ✅ STEP 9: CREATE AUTHENTICATION ENDPOINTS

### 🎯 Goal
Create REST endpoints for user registration, login, token refresh, and logout.

### 📚 What You Need to Know

**Authentication Flow:**

**1. Register:**
```
POST /api/auth/register
Body: {username, email, password}
→ Create user (no role)
→ Return success message
```

**2. Login:**
```
POST /api/auth/login
Body: {username, password}
→ Validate credentials
→ Generate access token + refresh token
→ Save refresh token to database
→ Return tokens + user info
```

**3. Refresh:**
```
POST /api/auth/refresh
Body: {refreshToken}
→ Validate refresh token
→ Generate new access token
→ Return new access token
```

**4. Logout:**
```
POST /api/auth/logout
Body: {refreshToken}
→ Revoke refresh token
→ Return success message
```

### ✍️ Implementation

#### 9.1 Create DTOs

**Location:** `src/main/java/com/restapi/gestion_bons/dto/auth/`

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
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
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

#### 9.2 Create RefreshTokenDAO

**Location:** `src/main/java/com/restapi/gestion_bons/dao/RefreshTokenDAO.java`

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

#### 9.3 Create RefreshTokenService

**Location:** `src/main/java/com/restapi/gestion_bons/service/security/RefreshTokenService.java`

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

#### 9.4 Create AuthService

**Location:** `src/main/java/com/restapi/gestion_bons/service/security/AuthService.java`

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
        // Check if username exists
        if (userDAO.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Check if email exists
        if (userDAO.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create user (no role assigned)
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
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Load user details
        UserApp user = userDAO.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // Generate tokens
        String accessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        // Build response
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

#### 9.5 Create AuthController

**Location:** `src/main/java/com/restapi/gestion_bons/controller/AuthController.java`

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

### ✅ Verification

1. **Start application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Test login (admin user):**
   ```bash
   curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password123"}'
   ```

   Should return:
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

3. **Test protected endpoint:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/produits \
     -H "Authorization: Bearer <your-access-token>"
   ```

---

## 🎉 Phase 3 Complete!

You now have:
- ✅ Spring Security configured
- ✅ Authentication endpoints (register, login, refresh, logout)
- ✅ JWT-based authentication working

**Next:** Phase 4 - Authorization (Steps 10-12)

