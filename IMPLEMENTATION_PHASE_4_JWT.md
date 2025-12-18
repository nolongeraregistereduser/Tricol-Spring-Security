# 🚀 PHASE 4: JWT INFRASTRUCTURE

## MINI-PHASE 4.1: Add JWT Configuration (5 minutes)

**What:** Add JWT settings to application.properties

**Action:**
1. Open file: `src/main/resources/application.properties`
2. Add these lines at the end:

```properties
# JWT Configuration
jwt.secret=5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437
jwt.access-token-expiration=900000
jwt.refresh-token-expiration=604800000
```

**Explanation:**
- `jwt.secret`: Secret key for signing tokens (64 hex characters)
- `jwt.access-token-expiration`: 15 minutes (900,000 ms)
- `jwt.refresh-token-expiration`: 7 days (604,800,000 ms)

3. Save file
4. ✅ Verify: Properties added

---

## MINI-PHASE 4.2: Create JwtService (20 minutes)

**What:** Service to generate and validate JWT tokens

**Action:**
1. Create folder: `src/main/java/com/restapi/gestion_bons/service/security/`
2. Create file: `JwtService.java` in that folder
3. Copy this code:

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

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAccessToken(UserDetails userDetails) {
        return generateAccessToken(new HashMap<>(), userDetails);
    }

    public String generateAccessToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, accessTokenExpiration);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, refreshTokenExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
```

4. Save file
5. ✅ Verify: Compiles successfully

---

## MINI-PHASE 4.3: Create UserDAO (10 minutes)

**What:** Repository to access users from database

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/dao/UserDAO.java`
2. Copy this code:

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

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 4.4: Create CustomUserDetailsService (20 minutes)

**What:** Load user from database and convert to Spring Security format

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/service/security/CustomUserDetailsService.java`
2. Copy this code:

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

    private Set<GrantedAuthority> getAuthorities(UserApp user) {
        Set<String> permissions = new HashSet<>();

        // Get default permissions from role
        if (user.getRole() != null && user.getRole().getDefaultPermissions() != null) {
            Set<String> rolePermissions = user.getRole().getDefaultPermissions()
                    .stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            permissions.addAll(rolePermissions);
        }

        // Apply custom permission overrides
        if (user.getCustomPermissions() != null) {
            for (UserPermission userPermission : user.getCustomPermissions()) {
                String permissionName = userPermission.getPermission().getName();
                if (userPermission.getGranted()) {
                    permissions.add(permissionName);
                } else {
                    permissions.remove(permissionName);
                }
            }
        }

        // Add role as authority
        if (user.getRole() != null) {
            permissions.add("ROLE_" + user.getRole().getName().name());
        }

        return permissions.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 4.5: Create JwtAuthenticationFilter (20 minutes)

**What:** Filter to intercept requests and validate JWT

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/config/JwtAuthenticationFilter.java`
2. Copy this code:

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
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String jwt = authHeader.substring(7);
        
        try {
            final String username = jwtService.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT authentication failed: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

## MINI-PHASE 4.6: Test Compilation (5 minutes)

**What:** Verify all JWT components compile

**Action:**
1. Run: `mvn clean compile`
2. ✅ Should compile without errors
3. If errors, check:
   - All imports are correct
   - JwtService is in correct package
   - CustomUserDetailsService is in correct package
   - JwtAuthenticationFilter is in config package

---

## 🎉 PHASE 4 CHECKPOINT

**What you've completed:**
- ✅ JWT configuration in properties
- ✅ JwtService (generate & validate tokens)
- ✅ UserDAO (database access)
- ✅ CustomUserDetailsService (load users with permissions)
- ✅ JwtAuthenticationFilter (intercept requests)

**Next:** Configure Spring Security

---
