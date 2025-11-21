# ✅ ADMIN MANAGEMENT ENDPOINTS

## Overview

Admin endpoints allow administrators to manage users, assign roles, and customize permissions.

**Base URL:** `/api/v1/admin/users`

**Required Permission:** `USER_MANAGE` (Only ADMIN role has this)

---

## Endpoints

### 1. Get All Users
**GET** `/api/v1/admin/users`

Returns list of all users with their roles and permissions.

**Response:**
```json
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@tricol.com",
    "enabled": true,
    "accountNonLocked": true,
    "role": "ADMIN",
    "createdAt": "2024-01-15T10:30:00",
    "permissions": ["PRODUIT_VIEW", "PRODUIT_CREATE", ...]
  }
]
```

---

### 2. Get User by ID
**GET** `/api/v1/admin/users/{userId}`

Returns details of a specific user.

**Response:**
```json
{
  "id": 2,
  "username": "ahmed",
  "email": "ahmed@tricol.com",
  "enabled": true,
  "accountNonLocked": true,
  "role": "MAGASINIER",
  "createdAt": "2024-01-16T09:00:00",
  "permissions": ["PRODUIT_VIEW", "STOCK_VIEW", "BON_SORTIE_CREATE"]
}
```

---

### 3. Assign Role to User
**PUT** `/api/v1/admin/users/{userId}/role`

Assigns a role to a user. User inherits all default permissions from the role.

**Request Body:**
```json
{
  "roleId": 3
}
```

**Response:**
```json
{
  "id": 2,
  "username": "ahmed",
  "email": "ahmed@tricol.com",
  "role": "MAGASINIER",
  "permissions": ["PRODUIT_VIEW", "STOCK_VIEW", ...]
}
```

**Example:**
```bash
curl -X PUT http://localhost:8080/api/v1/admin/users/2/role \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"roleId": 3}'
```

---

### 4. Customize User Permission
**PUT** `/api/v1/admin/users/{userId}/permissions`

Grant or revoke a specific permission for a user, overriding their role defaults.

**Request Body:**
```json
{
  "permissionId": 17,
  "granted": false
}
```

**Parameters:**
- `permissionId`: ID of the permission to customize
- `granted`: `true` to grant, `false` to revoke

**Response:**
```json
{
  "id": 2,
  "username": "ahmed",
  "email": "ahmed@tricol.com",
  "role": "MAGASINIER",
  "permissions": ["PRODUIT_VIEW", "STOCK_VIEW"]
}
```

**Example - Remove BON_SORTIE_CREATE permission:**
```bash
curl -X PUT http://localhost:8080/api/v1/admin/users/2/permissions \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"permissionId": 17, "granted": false}'
```

**Example - Grant PRODUIT_DELETE permission:**
```bash
curl -X PUT http://localhost:8080/api/v1/admin/users/2/permissions \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"permissionId": 4, "granted": true}'
```

---

### 5. Get All Roles
**GET** `/api/v1/admin/users/roles`

Returns list of all available roles with their default permissions.

**Response:**
```json
[
  {
    "id": 1,
    "name": "ADMIN",
    "description": "System administrator",
    "permissions": ["PRODUIT_VIEW", "PRODUIT_CREATE", ...]
  },
  {
    "id": 2,
    "name": "RESPONSABLE_ACHATS",
    "description": "Purchase manager",
    "permissions": ["PRODUIT_VIEW", "FOURNISSEUR_CREATE", ...]
  }
]
```

---

### 6. Get All Permissions
**GET** `/api/v1/admin/users/permissions`

Returns list of all available permissions in the system.

**Response:**
```json
[
  {
    "id": 1,
    "name": "PRODUIT_VIEW",
    "description": "View products",
    "resource": "PRODUIT",
    "action": "READ"
  },
  {
    "id": 2,
    "name": "PRODUIT_CREATE",
    "description": "Create products",
    "resource": "PRODUIT",
    "action": "CREATE"
  }
]
```

---

## Use Cases

### Use Case 1: New Employee Registration

**Scenario:** A new employee "Fatima" registers in the system.

**Steps:**
1. Fatima registers:
```bash
POST /api/auth/register
{
  "username": "fatima",
  "email": "fatima@tricol.com",
  "password": "secure123"
}
```

2. Admin assigns MAGASINIER role:
```bash
PUT /api/v1/admin/users/3/role
{
  "roleId": 3
}
```

3. Fatima can now access all MAGASINIER endpoints!

---

### Use Case 2: Custom Permission Override

**Scenario:** Ahmed (MAGASINIER) should NOT be able to create exit vouchers.

**Steps:**
1. Admin gets permission list to find BON_SORTIE_CREATE ID:
```bash
GET /api/v1/admin/users/permissions
# Find: {"id": 17, "name": "BON_SORTIE_CREATE"}
```

2. Admin revokes permission:
```bash
PUT /api/v1/admin/users/2/permissions
{
  "permissionId": 17,
  "granted": false
}
```

3. Ahmed tries to create exit voucher → 403 Forbidden ✓

---

### Use Case 3: Temporary Permission Grant

**Scenario:** Give CHEF_ATELIER temporary access to create products.

**Steps:**
1. Find PRODUIT_CREATE permission ID (id: 2)

2. Grant permission:
```bash
PUT /api/v1/admin/users/4/permissions
{
  "permissionId": 2,
  "granted": true
}
```

3. User can now create products even though CHEF_ATELIER role doesn't have this by default

4. Later, revoke it:
```bash
PUT /api/v1/admin/users/4/permissions
{
  "permissionId": 2,
  "granted": false
}
```

---

## Permission IDs Reference

| ID | Permission Name | Description |
|----|----------------|-------------|
| 1 | PRODUIT_VIEW | View products |
| 2 | PRODUIT_CREATE | Create products |
| 3 | PRODUIT_UPDATE | Update products |
| 4 | PRODUIT_DELETE | Delete products |
| 5 | STOCK_VIEW | View stock |
| 6 | STOCK_MANAGE | Manage stock |
| 7 | FOURNISSEUR_VIEW | View suppliers |
| 8 | FOURNISSEUR_CREATE | Create suppliers |
| 9 | FOURNISSEUR_UPDATE | Update suppliers |
| 10 | FOURNISSEUR_DELETE | Delete suppliers |
| 11 | COMMANDE_VIEW | View orders |
| 12 | COMMANDE_CREATE | Create orders |
| 13 | COMMANDE_UPDATE | Update orders |
| 14 | COMMANDE_VALIDATE | Validate orders |
| 15 | COMMANDE_RECEIVE | Receive orders |
| 16 | BON_SORTIE_VIEW | View exit vouchers |
| 17 | BON_SORTIE_CREATE | Create exit vouchers |
| 18 | BON_SORTIE_UPDATE | Update exit vouchers |
| 19 | BON_SORTIE_VALIDATE | Validate exit vouchers |
| 20 | BON_SORTIE_CANCEL | Cancel exit vouchers |
| 21 | ATELIER_VIEW | View workshops |
| 22 | ATELIER_CREATE | Create workshops |
| 23 | ATELIER_UPDATE | Update workshops |
| 24 | USER_VIEW | View users |
| 25 | USER_MANAGE | Manage users |

## Role IDs Reference

| ID | Role Name | Description |
|----|-----------|-------------|
| 1 | ADMIN | System administrator |
| 2 | RESPONSABLE_ACHATS | Purchase manager |
| 3 | MAGASINIER | Warehouse keeper |
| 4 | CHEF_ATELIER | Workshop manager |

---

## Testing

### 1. Login as Admin
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}'
```

### 2. Get All Users
```bash
curl -X GET http://localhost:8080/api/v1/admin/users \
  -H "Authorization: Bearer ADMIN_TOKEN"
```

### 3. Assign Role
```bash
curl -X PUT http://localhost:8080/api/v1/admin/users/2/role \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"roleId": 3}'
```

### 4. Customize Permission
```bash
curl -X PUT http://localhost:8080/api/v1/admin/users/2/permissions \
  -H "Authorization: Bearer ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"permissionId": 17, "granted": false}'
```

---

## Security

- All endpoints require `USER_MANAGE` permission
- Only ADMIN role has this permission by default
- Permission changes are tracked (modifiedBy field)
- All operations are transactional

---

## Next Steps

Now you can:
1. ✅ Register new users
2. ✅ Assign roles to users
3. ✅ Customize individual permissions
4. ✅ View all users, roles, and permissions

**Your admin management system is complete!** 🎉
