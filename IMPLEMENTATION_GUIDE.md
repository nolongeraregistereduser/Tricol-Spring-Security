# 🚀 IMPLEMENTATION GUIDE - START HERE

## 📋 HOW TO USE THIS GUIDE

This guide breaks down the entire project into small, actionable steps. Follow them in order.

**For each step:**
1. Read the explanation
2. Copy the code
3. Create the file
4. Verify it works
5. Move to next step

---

## ✅ PHASE 1: FOUNDATION

### MINI-PHASE 1.1: Verify Dependencies (5 minutes)

**What:** Check that JWT dependencies are in pom.xml

**Action:**
1. Open `pom.xml`
2. Verify these dependencies exist (they already do):
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

3. Run: `mvn clean install`
4. ✅ Should compile successfully

---

### MINI-PHASE 1.2: Create RoleName Enum (5 minutes)

**What:** Define the 4 roles as an enum

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/enums/RoleName.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie.enums;

public enum RoleName {
    ADMIN,
    RESPONSABLE_ACHATS,
    MAGASINIER,
    CHEF_ATELIER
}
```

3. Save file
4. ✅ Verify: File compiles without errors

---

### MINI-PHASE 1.3: Create Permission Entity (10 minutes)

**What:** Create entity to store permissions

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/Permission.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String resource;

    @Column(length = 20)
    private String action;

    @ManyToMany(mappedBy = "defaultPermissions")
    @Builder.Default
    private Set<RoleApp> roles = new HashSet<>();

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserPermission> userPermissions = new HashSet<>();
}
```

3. Save file
4. ✅ Verify: Compiles (ignore RoleApp and UserPermission errors for now)

---

### MINI-PHASE 1.4: Create RoleApp Entity (10 minutes)

**What:** Create entity to store roles

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/RoleApp.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie;

import com.restapi.gestion_bons.entitie.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "role_app")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleName name;

    @Column(length = 255)
    private String description;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permission",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    @Builder.Default
    private Set<Permission> defaultPermissions = new HashSet<>();

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserApp> users = new HashSet<>();
}
```

3. Save file
4. ✅ Verify: Compiles (ignore UserApp error for now)

---

### MINI-PHASE 1.5: Create UserApp Entity (10 minutes)

**What:** Create entity to store users

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/UserApp.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_app")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    @Builder.Default
    private Boolean enabled = true;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private RoleApp role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<UserPermission> customPermissions = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<RefreshToken> refreshTokens = new HashSet<>();
}
```

3. Save file
4. ✅ Verify: Compiles (ignore UserPermission and RefreshToken errors)

---

### MINI-PHASE 1.6: Create UserPermission Entity (10 minutes)

**What:** Create entity for custom user permissions

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/UserPermission.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_permission", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "permission_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserApp user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;

    @Column(nullable = false)
    private Boolean granted;

    @Column(name = "modified_at")
    @Builder.Default
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Column(name = "modified_by")
    private String modifiedBy;
}
```

3. Save file
4. ✅ Verify: Compiles

---

### MINI-PHASE 1.7: Create RefreshToken Entity (10 minutes)

**What:** Create entity for refresh tokens

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/RefreshToken.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserApp user;

    @Column(nullable = false)
    @Builder.Default
    private Boolean revoked = false;
}
```

3. Save file
4. ✅ Verify: All entities compile successfully

---

### MINI-PHASE 1.8: Create AuditLog Entity (10 minutes)

**What:** Create entity for audit logging

**Action:**
1. Create file: `src/main/java/com/restapi/gestion_bons/entitie/AuditLog.java`
2. Copy this code:

```java
package com.restapi.gestion_bons.entitie;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_timestamp", columnList = "timestamp"),
    @Index(name = "idx_action", columnList = "action")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 50)
    private String username;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(length = 50)
    private String resource;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String details;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
```

3. Save file
4. ✅ Verify: Compiles successfully

---

### MINI-PHASE 1.9: Compile All Entities (5 minutes)

**What:** Verify all entities are created correctly

**Action:**
1. Run: `mvn clean compile`
2. ✅ Should compile without errors
3. If errors, check:
   - All imports are correct
   - No typos in class names
   - All files are in correct packages

---

## 🎉 PHASE 1 CHECKPOINT

**What you've completed:**
- ✅ RoleName enum
- ✅ Permission entity
- ✅ RoleApp entity
- ✅ UserApp entity
- ✅ UserPermission entity
- ✅ RefreshToken entity
- ✅ AuditLog entity

**Next:** Create database migrations

---
