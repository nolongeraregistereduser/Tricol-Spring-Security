# 📋 STEPS 5-7: JWT INFRASTRUCTURE

---

## ✅ STEP 5: CREATE JWT SERVICE

### 🎯 Goal
Build a service to generate, validate, and extract information from JWT tokens.

### 📚 What You Need to Know

**JWT Service Responsibilities:**
1. Generate access tokens (short-lived, 15 minutes)
2. Generate refresh tokens (long-lived, 7 days)
3. Extract username from token
4. Validate token (signature + expiration)
5. Extract all claims (user info) from token

**Key Concepts:**

**Claims:** Data stored in JWT payload
- `sub` (subject): Username
- `iat` (issued at): Creation timestamp
- `exp` (expiration): Expiry timestamp
- Custom claims: roles, permissions, etc.

**Secret Key:** Used to sign tokens
- Must be kept secret
- Should be long and random
- Store in application.properties

### ✍️ Implementation

#### 5.1 Add JWT Configuration to application.properties

**Location:** `src/main/resources/application.properties`

Add these properties:

```properties
# JWT Configuration
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000
```

**Explanation:**
- `jwt.secret`: Secret key for signing (64 characters hex)
- `jwt.access-token-expiration`: 15 minutes in milliseconds (15 * 60 * 1000)
- `jwt.refresh-token-expiration`: 7 days in milliseconds (7 * 24 * 60 * 60 * 1000)

**Generate your own secret:**
```java
// Run this to generate a secure secret
SecureRandom random = new SecureRandom();
byte[] bytes = new byte[32];
random.nextBytes(bytes);
String secret = Hex.encodeHexString(bytes);
```

#### 5.2 Create JwtService

**Location:** `src/main/java/com/restapi/gestion_bons/service/security/JwtService.java`

```java
package com.restapi.gestion_bons.service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    // Extract username from token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extract a specific claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // Generate access token
    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    // Generate access token with extra claims
    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    // Generate refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    // Build token with claims and expiration
    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration
    ) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extract expiration date
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Extract all claims from token
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Get signing key from secret
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

**Explanation:**

**Key Methods:**

1. **extractUsername(token)**
   - Extracts username from token's subject claim
   - Used to identify who the token belongs to

2. **generateAccessToken(userDetails)**
   - Creates short-lived token (15 min)
   - Contains username and expiration
   - Signed with secret key

3. **generateRefreshToken(userDetails)**
   - Creates long-lived token (7 days)
   - Used to get new access tokens

4. **isTokenValid(token, userDetails)**
   - Checks if token belongs to user
   - Checks if token is not expired
   - Returns true if valid

5. **buildToken(...)**
   - Core method that creates JWT
   - Sets claims (data)
   - Sets subject (username)
   - Sets issued at and expiration
   - Signs with secret key

**Token Structure Created:**
```
Header: {"alg": "HS256", "typ": "JWT"}
Payload: {"sub": "admin", "iat": 1705320000, "exp": 1705320900}
Signature: HMACSHA256(header + payload, secret)
```

---

## ✅ STEP 6: CREATE USER DETAILS SERVICE

### 🎯 Goal
Load user from database and convert to Spring Security's UserDetails format.

### 📚 What You Need to Know

**UserDetailsService:**
Spring Security interface with one method: `loadUserByUsername(String username)`

**UserDetails:**
Spring Security's representation of a user with:
- Username
- Password
- Authorities (permissions/roles)
- Account status (enabled, locked, etc.)

**Our Implementation:**
1. Load UserApp from database
2. Load user's permissions (role defaults + custom overrides)
3. Convert to UserDetails

### ✍️ Implementation

#### 6.1 Create UserDAO

**Location:** `src/main/java/com/restapi/gestion_bons/dao/UserDAO.java`

```java
package com.restapi.gestion_bons.dao;

import com.restapi.gestion_bons.entitie.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDAO extends JpaRepository<UserApp, Long> {
    
    Optional<UserApp> findByUsername(String username);
    
    Optional<UserApp> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM UserApp u LEFT JOIN FETCH u.role r LEFT JOIN FETCH r.defaultPermissions WHERE u.username = :username")
    Optional<UserApp> findByUsernameWithRoleAndPermissions(String username);
}
```

**Explanation:**
- `findByUsername`: Find user by username
- `findByUsernameWithRoleAndPermissions`: Eager load role and permissions (avoids N+1 queries)
- `existsByUsername`: Check if username is taken

#### 6.2 Create CustomUserDetailsService

**Location:** `src/main/java/com/restapi/gestion_bons/service/security/CustomUserDetailsService.java`

```java
package com.restapi.gestion_bons.service.security;

import com.restapi.gestion_bons.dao.UserDAO;
import com.restapi.gestion_bons.entitie.Permission;
import com.restapi.gestion_bons.entitie.UserApp;
import com.restapi.gestion_bons.entitie.UserPermission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDAO userDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = userDAO.findByUsernameWithRoleAndPermissions(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(getAuthorities(user))
                .accountExpired(false)
                .accountLocked(!user.getAccountNonLocked())
                .credentialsExpired(false)
                .disabled(!user.getEnabled())
                .build();
    }

    // Get user's effective permissions (role defaults + custom overrides)
    private Set<GrantedAuthority> getAuthorities(UserApp user) {
        Set<String> permissions = new HashSet<>();

        // 1. Get default permissions from role
        if (user.getRole() != null && user.getRole().getDefaultPermissions() != null) {
            Set<String> rolePermissions = user.getRole().getDefaultPermissions()
                    .stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            permissions.addAll(rolePermissions);
        }

        // 2. Apply custom permission overrides
        if (user.getCustomPermissions() != null) {
            for (UserPermission userPermission : user.getCustomPermissions()) {
                String permissionName = userPermission.getPermission().getName();
                if (userPermission.getGranted()) {
                    // Grant permission (add if not present)
                    permissions.add(permissionName);
                } else {
                    // Deny permission (remove if present)
                    permissions.remove(permissionName);
                }
            }
        }

        // 3. Add role as authority (for @PreAuthorize("hasRole('ADMIN')"))
        if (user.getRole() != null) {
            permissions.add("ROLE_" + user.getRole().getName().name());
        }

        // Convert to GrantedAuthority
        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
```

**Explanation:**

**loadUserByUsername Method:**
1. Load user from database with role and permissions
2. Build Spring Security UserDetails object
3. Set username, password, authorities
4. Set account status (enabled, locked)

**getAuthorities Method:**
This is the core of dynamic permissions!

1. **Start with role defaults:**
   ```
   User has MAGASINIER role
   → Get all MAGASINIER default permissions
   → Add to permissions set
   ```

2. **Apply custom overrides:**
   ```
   Check user_permission table
   If granted = true → Add permission
   If granted = false → Remove permission
   ```

3. **Add role as authority:**
   ```
   Add "ROLE_MAGASINIER" for role-based checks
   ```

**Example:**
```
User: Ahmed (MAGASINIER)
Role defaults: [STOCK_VIEW, BON_SORTIE_CREATE, BON_SORTIE_VIEW]
Custom overrides: [BON_SORTIE_CREATE = false]

Final authorities: [STOCK_VIEW, BON_SORTIE_VIEW, ROLE_MAGASINIER]
```

---

## ✅ STEP 7: CREATE JWT AUTHENTICATION FILTER

### 🎯 Goal
Create a filter that intercepts every request, extracts JWT, validates it, and sets authentication.

### 📚 What You Need to Know

**Filter Chain:**
Every HTTP request passes through a chain of filters before reaching your controller.

**Our Filter's Job:**
1. Extract JWT from Authorization header
2. Validate token
3. Load user details
4. Set authentication in SecurityContext
5. Continue to next filter

**Filter Position:**
Our filter runs BEFORE UsernamePasswordAuthenticationFilter.

### ✍️ Implementation

#### 7.1 Create JwtAuthenticationFilter

**Location:** `src/main/java/com/restapi/gestion_bons/config/JwtAuthenticationFilter.java`

```java
package com.restapi.gestion_bons.config;

import com.restapi.gestion_bons.service.security.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        
        // 1. Extract Authorization header
        final String authHeader = request.getHeader("Authorization");
        
        // 2. Check if header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract JWT token (remove "Bearer " prefix)
        final String jwt = authHeader.substring(7);
        
        try {
            // 4. Extract username from token
            final String username = jwtService.extractUsername(jwt);

            // 5. Check if user is not already authenticated
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // 6. Load user details from database
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // 7. Validate token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    
                    // 8. Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // 9. Set additional details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 10. Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Token is invalid, continue without authentication
            logger.error("JWT authentication failed: " + e.getMessage());
        }

        // 11. Continue filter chain
        filterChain.doFilter(request, response);
    }
}
```

**Explanation:**

**Step-by-Step Flow:**

1. **Extract Authorization header**
   ```
   Request header: "Authorization: Bearer eyJhbGc..."
   ```

2. **Check format**
   - Must start with "Bearer "
   - If not, skip authentication (public endpoint)

3. **Extract token**
   ```
   Remove "Bearer " prefix
   jwt = "eyJhbGc..."
   ```

4. **Extract username**
   ```
   Parse JWT and get subject claim
   username = "admin"
   ```

5. **Check if already authenticated**
   - Avoid re-authenticating on same request

6. **Load user from database**
   ```
   UserDetails = loadUserByUsername("admin")
   → Gets user with all permissions
   ```

7. **Validate token**
   - Check signature
   - Check expiration
   - Check username matches

8. **Create authentication object**
   ```
   UsernamePasswordAuthenticationToken contains:
   - Principal: UserDetails
   - Credentials: null (already authenticated)
   - Authorities: User's permissions
   ```

9. **Set request details**
   - IP address, session ID, etc.

10. **Set in SecurityContext**
    ```
    SecurityContextHolder stores authentication
    → Available to all subsequent filters and controllers
    ```

11. **Continue chain**
    - Pass request to next filter

**Request Flow Example:**
```
Client sends: GET /api/v1/produits
Header: Authorization: Bearer eyJhbGc...

↓ JwtAuthenticationFilter
  → Extract token
  → Validate token
  → Load user "admin"
  → Set authentication

↓ FilterSecurityInterceptor
  → Check if user has PRODUIT_VIEW permission
  → Allow or deny

↓ ProduitController
  → Process request
  → Return products
```

### ✅ Verification

After implementing Steps 5-7:

1. **Compile:**
   ```bash
   mvn clean compile
   ```

2. **Check beans:**
   - JwtService should be a Spring bean
   - CustomUserDetailsService should be a Spring bean
   - JwtAuthenticationFilter should be a Spring component

3. **Test JWT generation (create a test):**
   ```java
   @Test
   void testJwtGeneration() {
       UserDetails user = User.builder()
           .username("admin")
           .password("password")
           .authorities("ROLE_ADMIN")
           .build();
       
       String token = jwtService.generateAccessToken(user);
       assertNotNull(token);
       
       String username = jwtService.extractUsername(token);
       assertEquals("admin", username);
   }
   ```

---

## 🎉 Phase 2 Complete!

You now have:
- ✅ JWT service (generate & validate tokens)
- ✅ User details service (load users with permissions)
- ✅ JWT authentication filter (intercept & authenticate requests)

**Next:** Phase 3 - Security Configuration (Steps 8-9)

