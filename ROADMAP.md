# 🗺️ SPRING SECURITY + JWT IMPLEMENTATION ROADMAP

## 📌 Project: Tricol Stock Management System Security

---

## 🎯 PROJECT OVERVIEW

### What We're Building
You have an existing **Stock Management System** that handles:
- Products, Suppliers, Purchase Orders
- Exit Vouchers, Stock Movements, Workshops

**Mission**: Add complete security with JWT authentication and role-based authorization.

### What You'll Learn
- Spring Security fundamentals
- JWT token authentication
- Role-Based Access Control (RBAC)
- Dynamic permission management
- Audit logging
- Docker containerization
- CI/CD with GitHub Actions

---

## 📚 PHASE 0: CORE CONCEPTS (READ THIS FIRST!)

### 🔐 Concept 1: Authentication vs Authorization

**Authentication** = "Who are you?"
- Verifying user identity (username + password)
- Like showing your ID card at the entrance

**Authorization** = "What can you do?"
- Checking permissions after authentication
- Like checking if your ID card allows access to specific rooms

**Example:**
```
User "Ahmed" logs in → Authentication ✓
Ahmed tries to delete a product → Check if Ahmed has "PRODUIT_DELETE" permission → Authorization
```

---

### 🎫 Concept 2: JWT (JSON Web Token)

**What is JWT?**
A secure way to transmit information between client and server as a JSON object.

**Structure:**
```
xxxxx.yyyyy.zzzzz
Header.Payload.Signature
```

**1. Header** (Algorithm info)
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**2. Payload** (User data)
```json
{
  "sub": "ahmed@tricol.com",
  "roles": ["MAGASINIER"],
  "exp": 1735689600
}
```

**3. Signature** (Security)
```
HMACSHA256(
  base64UrlEncode(header) + "." + base64UrlEncode(payload),
  secret_key
)
```

**Why JWT for REST APIs?**
- ✅ Stateless (no server-side sessions)
- ✅ Scalable (works across multiple servers)
- ✅ Self-contained (all info in the token)
- ✅ Secure (signed and optionally encrypted)

**JWT Flow:**
```
1. User logs in with username/password
2. Server validates credentials
3. Server generates JWT token
4. Client stores token (localStorage/cookie)
5. Client sends token in every request: Authorization: Bearer <token>
6. Server validates token and processes request
```

---

### 👥 Concept 3: Roles and Permissions

**Role** = A job title with default permissions
- ADMIN, RESPONSABLE_ACHATS, MAGASINIER, CHEF_ATELIER

**Permission** = A specific action on a resource
- Format: `RESOURCE_ACTION`
- Examples: `PRODUIT_CREATE`, `STOCK_VIEW`, `BON_SORTIE_DELETE`

**Permission Structure:**
```
PRODUIT_CREATE
  ↓       ↓
Resource Action
```

**Your System's Roles:**

1. **ADMIN** - System administrator
   - Full access to everything
   - Manage users and permissions

2. **RESPONSABLE_ACHATS** - Purchase manager
   - Manage suppliers
   - Create/validate purchase orders
   - View stock

3. **MAGASINIER** - Warehouse keeper
   - Receive orders
   - Create exit vouchers
   - Manage stock movements

4. **CHEF_ATELIER** - Workshop manager
   - View exit vouchers for their workshop
   - Request materials

---

### 🎛️ Concept 4: Dynamic Permission System

**The Problem:**
Traditional RBAC is rigid. If a MAGASINIER shouldn't create exit vouchers, you'd need a new role.

**The Solution:**
Dynamic permissions allow customization per user.

**How It Works:**

1. **Default Permissions** (from role)
```
MAGASINIER role → Default permissions:
  - STOCK_VIEW ✓
  - BON_SORTIE_CREATE ✓
  - BON_SORTIE_VIEW ✓
```

2. **Custom Override** (per user)
```
Admin removes BON_SORTIE_CREATE from user "Ahmed"
  - STOCK_VIEW ✓
  - BON_SORTIE_CREATE ✗ (overridden)
  - BON_SORTIE_VIEW ✓
```

**Database Design:**
```
User → has → Role → has → Default Permissions
User → has → Custom Permissions (overrides)
```

**Permission Check Logic:**
```java
if (user has custom permission for this action) {
    return custom permission value;
} else {
    return role default permission value;
}
```

---

### 🔒 Concept 5: Spring Security Filter Chain

**What is it?**
A series of filters that process every HTTP request before it reaches your controller.

**Filter Chain Flow:**
```
HTTP Request
    ↓
[1] CorsFilter (Handle CORS)
    ↓
[2] JwtAuthenticationFilter (Extract & validate JWT)
    ↓
[3] UsernamePasswordAuthenticationFilter
    ↓
[4] FilterSecurityInterceptor (Check permissions)
    ↓
Your Controller
```

**Our Custom Filter (JwtAuthenticationFilter):**
```
1. Extract JWT from Authorization header
2. Validate token signature
3. Extract username from token
4. Load user details from database
5. Set authentication in SecurityContext
6. Continue to next filter
```

---

### 🛡️ Concept 6: Spring Security Core Components

**1. SecurityContext**
- Holds authentication information for current request
- Thread-local storage (one per request)

**2. Authentication**
- Represents authenticated user
- Contains: principal (user), credentials (password), authorities (permissions)

**3. UserDetails**
- Spring Security's representation of a user
- Interface with: username, password, authorities, account status

**4. UserDetailsService**
- Loads user from database
- Converts your User entity → UserDetails

**5. AuthenticationManager**
- Authenticates users
- Validates username/password

**6. PasswordEncoder**
- Encrypts passwords (BCrypt)
- Validates encrypted passwords

---

### 📊 Concept 7: Audit Logging

**Why Audit?**
- Track who did what and when
- Security compliance
- Debugging and troubleshooting
- Detect suspicious activity

**What to Log:**
- Authentication events (login, logout, failed attempts)
- CRUD operations on sensitive data
- Permission changes
- Configuration changes

**Audit Entry Example:**
```json
{
  "timestamp": "2024-01-15T10:30:00",
  "userId": 5,
  "username": "ahmed",
  "action": "DELETE",
  "resource": "PRODUIT",
  "resourceId": 123,
  "ipAddress": "192.168.1.100",
  "details": "Deleted product: Laptop HP"
}
```

---

## 🚀 IMPLEMENTATION PHASES

### Phase 1: Foundation (Steps 1-4)
- Add dependencies
- Create security entities
- Database migrations
- Seed initial data

### Phase 2: JWT Infrastructure (Steps 5-7)
- JWT service
- User details service
- JWT filter

### Phase 3: Security Configuration (Steps 8-9)
- Configure Spring Security
- Authentication endpoints

### Phase 4: Authorization (Steps 10-12)
- Permission service
- Admin endpoints
- Secure existing endpoints

### Phase 5: Audit & Testing (Steps 13-14)
- Audit logging
- Unit tests

### Phase 6: Deployment (Steps 15-16)
- Dockerization
- CI/CD pipeline

---

