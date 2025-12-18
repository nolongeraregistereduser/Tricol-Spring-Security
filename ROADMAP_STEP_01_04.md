# 📋 STEPS 1-4: FOUNDATION

---

## ✅ STEP 1: ADD JWT DEPENDENCIES

### 🎯 Goal
Add JWT libraries to your project so you can create and validate JWT tokens.

### 📚 What You Need to Know

**Maven Dependencies:**
Maven is your project's dependency manager. The `pom.xml` file lists all libraries your project needs.

**JWT Library (JJWT):**
- `jjwt-api`: Core JWT interfaces
- `jjwt-impl`: Implementation (runtime only)
- `jjwt-jackson`: JSON processing (runtime only)

### ✍️ Implementation

**Status:** ✅ ALREADY DONE! (I can see JWT dependencies in your pom.xml)

Your pom.xml already has:
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

### ✅ Verification
Run: `mvn clean install`
- Should compile without errors
- JWT classes should be available

---

## ✅ STEP 2: CREATE SECURITY ENTITIES

### 🎯 Goal
Create database entities for users, roles, permissions, and audit logs.

### 📚 What You Need to Know

**JPA Entity:**
A Java class that maps to a database table.

**Annotations:**
- `@Entity`: Marks class as database table
- `@Table`: Specifies table name
- `@Id`: Primary key
- `@GeneratedValue`: Auto-increment
- `@Column`: Column configuration
- `@ManyToOne`: Many entities reference one
- `@OneToMany`: One entity has many
- `@ManyToMany`: Many-to-many relationship

**Lombok Annotations:**
- `@Getter/@Setter`: Auto-generate getters/setters
- `@NoArgsConstructor`: Empty constructor
- `@AllArgsConstructor`: Constructor with all fields
- `@Builder`: Builder pattern for object creation

### ✍️ Implementation

#### 2.1 Create RoleApp Entity

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/RoleApp.java`

**Concept:** Represents user roles (ADMIN, MAGASINIER, etc.)

```java
package com.restapi.gestion_bons.entitie;

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

**Explanation:**
- `RoleName`: Enum for role names (we'll create next)
- `defaultPermissions`: Permissions that come with this role
- `users`: All users with this role
- `@ManyToMany`: One role has many permissions, one permission belongs to many roles

#### 2.2 Create RoleName Enum

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/enums/RoleName.java`

```java
package com.restapi.gestion_bons.entitie.enums;

public enum RoleName {
    ADMIN,
    RESPONSABLE_ACHATS,
    MAGASINIER,
    CHEF_ATELIER
}
```

**Why Enum?**
- Type-safe (can't assign invalid role)
- Prevents typos
- Easy to maintain

#### 2.3 Create Permission Entity

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/Permission.java`

**Concept:** Represents a specific action (e.g., PRODUIT_CREATE)

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
    private String name; // e.g., "PRODUIT_CREATE"

    @Column(length = 255)
    private String description;

    @Column(length = 50)
    private String resource; // e.g., "PRODUIT", "STOCK"

    @Column(length = 20)
    private String action; // e.g., "CREATE", "READ", "UPDATE", "DELETE"

    @ManyToMany(mappedBy = "defaultPermissions")
    @Builder.Default
    private Set<RoleApp> roles = new HashSet<>();

    @OneToMany(mappedBy = "permission", cascade = CascadeType.ALL)
    @Builder.Default
    private Set<UserPermission> userPermissions = new HashSet<>();
}
```

**Explanation:**
- `name`: Unique identifier (PRODUIT_CREATE)
- `resource`: What entity (PRODUIT, STOCK, BON_SORTIE)
- `action`: What operation (CREATE, READ, UPDATE, DELETE)
- `roles`: Which roles have this permission by default
- `userPermissions`: Custom user overrides

#### 2.4 Create UserApp Entity

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/UserApp.java`

**Concept:** Represents a system user

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
    private String password; // BCrypt encrypted

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

**Explanation:**
- `password`: Stored encrypted (never plain text!)
- `enabled`: Can user log in?
- `accountNonLocked`: Is account locked?
- `role`: User's assigned role (can be null for new users)
- `customPermissions`: Overrides for this specific user

#### 2.5 Create UserPermission Entity

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/UserPermission.java`

**Concept:** Custom permission override for a specific user

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
    private Boolean granted; // true = has permission, false = denied

    @Column(name = "modified_at")
    @Builder.Default
    private LocalDateTime modifiedAt = LocalDateTime.now();

    @Column(name = "modified_by")
    private String modifiedBy; // Admin who made the change
}
```

**Explanation:**
- `granted`: true = user HAS this permission, false = user DOESN'T have it
- `modifiedAt`: When was this override created?
- `modifiedBy`: Which admin made this change?
- `uniqueConstraints`: One user can't have duplicate permission entries

**Example:**
```
User: Ahmed (MAGASINIER role)
Permission: BON_SORTIE_CREATE
Granted: false
→ Ahmed can't create exit vouchers even though his role normally allows it
```

#### 2.6 Create RefreshToken Entity

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/RefreshToken.java`

**Concept:** Long-lived token to get new access tokens

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

**Why Refresh Tokens?**
- Access tokens expire quickly (15 minutes)
- Refresh tokens last longer (7 days)
- User doesn't need to re-login every 15 minutes
- Can be revoked for security

**Flow:**
```
1. User logs in → Get access token (15 min) + refresh token (7 days)
2. After 15 min, access token expires
3. Client sends refresh token → Get new access token
4. Repeat until refresh token expires or user logs out
```

#### 2.7 Create AuditLog Entity

**Location:** `src/main/java/com/restapi/gestion_bons/entitie/AuditLog.java`

**Concept:** Track all important actions in the system

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
    private String action; // LOGIN, LOGOUT, CREATE, UPDATE, DELETE

    @Column(length = 50)
    private String resource; // PRODUIT, STOCK, BON_SORTIE, USER

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(columnDefinition = "TEXT")
    private String details; // JSON or text description

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();
}
```

**Explanation:**
- `@Index`: Speed up queries on these columns
- `action`: What happened (LOGIN, CREATE, DELETE, etc.)
- `resource`: What entity was affected
- `resourceId`: Which specific record
- `details`: Additional info (JSON format)

**Example Entry:**
```
userId: 5
username: "ahmed"
action: "DELETE"
resource: "PRODUIT"
resourceId: 123
ipAddress: "192.168.1.100"
details: '{"productName": "Laptop HP", "reason": "Discontinued"}'
timestamp: 2024-01-15 10:30:00
```

### ✅ Verification

After creating all entities:

1. **Check compilation:**
   ```bash
   mvn clean compile
   ```
   Should compile without errors.

2. **Check entity relationships:**
   - UserApp → RoleApp (ManyToOne)
   - UserApp → UserPermission (OneToMany)
   - RoleApp → Permission (ManyToMany)
   - UserApp → RefreshToken (OneToMany)

---

