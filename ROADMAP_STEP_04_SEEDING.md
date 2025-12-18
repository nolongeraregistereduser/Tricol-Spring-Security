# 📋 STEP 4: SEED INITIAL DATA

---

## ✅ STEP 4: SEED ROLES AND PERMISSIONS

### 🎯 Goal
Populate the database with initial roles and permissions so the system is ready to use.

### 📚 What You Need to Know

**Database Seeding:**
Pre-populating database with essential data.

**What to Seed:**
1. **Permissions** - All possible actions in the system
2. **Roles** - The 4 user roles
3. **Role-Permission Mappings** - Default permissions for each role
4. **Admin User** - First user to manage the system

**Two Approaches:**
1. **Liquibase Migration** (recommended for production)
2. **Spring Boot Seeder** (good for development)

We'll use Liquibase for consistency.

### ✍️ Implementation

#### 4.1 Create Permissions Seed Migration

**Location:** `src/main/resources/db/changelog/changes/v017-seed-permissions.yaml`

**Concept:** Define all permissions in the system

```yaml
databaseChangeLog:
  - changeSet:
      id: v017-seed-permissions
      author: tricol-security
      changes:
        # PRODUIT Permissions
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "PRODUIT_VIEW"}
              - column: {name: description, value: "View products"}
              - column: {name: resource, value: "PRODUIT"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "PRODUIT_CREATE"}
              - column: {name: description, value: "Create products"}
              - column: {name: resource, value: "PRODUIT"}
              - column: {name: action, value: "CREATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "PRODUIT_UPDATE"}
              - column: {name: description, value: "Update products"}
              - column: {name: resource, value: "PRODUIT"}
              - column: {name: action, value: "UPDATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "PRODUIT_DELETE"}
              - column: {name: description, value: "Delete products"}
              - column: {name: resource, value: "PRODUIT"}
              - column: {name: action, value: "DELETE"}
        
        # STOCK Permissions
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "STOCK_VIEW"}
              - column: {name: description, value: "View stock"}
              - column: {name: resource, value: "STOCK"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "STOCK_MANAGE"}
              - column: {name: description, value: "Manage stock movements"}
              - column: {name: resource, value: "STOCK"}
              - column: {name: action, value: "UPDATE"}
        
        # FOURNISSEUR Permissions
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "FOURNISSEUR_VIEW"}
              - column: {name: description, value: "View suppliers"}
              - column: {name: resource, value: "FOURNISSEUR"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "FOURNISSEUR_CREATE"}
              - column: {name: description, value: "Create suppliers"}
              - column: {name: resource, value: "FOURNISSEUR"}
              - column: {name: action, value: "CREATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "FOURNISSEUR_UPDATE"}
              - column: {name: description, value: "Update suppliers"}
              - column: {name: resource, value: "FOURNISSEUR"}
              - column: {name: action, value: "UPDATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "FOURNISSEUR_DELETE"}
              - column: {name: description, value: "Delete suppliers"}
              - column: {name: resource, value: "FOURNISSEUR"}
              - column: {name: action, value: "DELETE"}
        
        # COMMANDE_FOURNISSEUR Permissions
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "COMMANDE_VIEW"}
              - column: {name: description, value: "View purchase orders"}
              - column: {name: resource, value: "COMMANDE_FOURNISSEUR"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "COMMANDE_CREATE"}
              - column: {name: description, value: "Create purchase orders"}
              - column: {name: resource, value: "COMMANDE_FOURNISSEUR"}
              - column: {name: action, value: "CREATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "COMMANDE_UPDATE"}
              - column: {name: description, value: "Update purchase orders"}
              - column: {name: resource, value: "COMMANDE_FOURNISSEUR"}
              - column: {name: action, value: "UPDATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "COMMANDE_VALIDATE"}
              - column: {name: description, value: "Validate purchase orders"}
              - column: {name: resource, value: "COMMANDE_FOURNISSEUR"}
              - column: {name: action, value: "VALIDATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "COMMANDE_RECEIVE"}
              - column: {name: description, value: "Receive purchase orders"}
              - column: {name: resource, value: "COMMANDE_FOURNISSEUR"}
              - column: {name: action, value: "RECEIVE"}
        
        # BON_SORTIE Permissions
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "BON_SORTIE_VIEW"}
              - column: {name: description, value: "View exit vouchers"}
              - column: {name: resource, value: "BON_SORTIE"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "BON_SORTIE_CREATE"}
              - column: {name: description, value: "Create exit vouchers"}
              - column: {name: resource, value: "BON_SORTIE"}
              - column: {name: action, value: "CREATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "BON_SORTIE_UPDATE"}
              - column: {name: description, value: "Update exit vouchers"}
              - column: {name: resource, value: "BON_SORTIE"}
              - column: {name: action, value: "UPDATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "BON_SORTIE_VALIDATE"}
              - column: {name: description, value: "Validate exit vouchers"}
              - column: {name: resource, value: "BON_SORTIE"}
              - column: {name: action, value: "VALIDATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "BON_SORTIE_CANCEL"}
              - column: {name: description, value: "Cancel exit vouchers"}
              - column: {name: resource, value: "BON_SORTIE"}
              - column: {name: action, value: "CANCEL"}
        
        # ATELIER Permissions
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "ATELIER_VIEW"}
              - column: {name: description, value: "View workshops"}
              - column: {name: resource, value: "ATELIER"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "ATELIER_CREATE"}
              - column: {name: description, value: "Create workshops"}
              - column: {name: resource, value: "ATELIER"}
              - column: {name: action, value: "CREATE"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "ATELIER_UPDATE"}
              - column: {name: description, value: "Update workshops"}
              - column: {name: resource, value: "ATELIER"}
              - column: {name: action, value: "UPDATE"}
        
        # USER_MANAGEMENT Permissions (Admin only)
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "USER_VIEW"}
              - column: {name: description, value: "View users"}
              - column: {name: resource, value: "USER"}
              - column: {name: action, value: "READ"}
        
        - insert:
            tableName: permission
            columns:
              - column: {name: name, value: "USER_MANAGE"}
              - column: {name: description, value: "Manage users and permissions"}
              - column: {name: resource, value: "USER"}
              - column: {name: action, value: "UPDATE"}
```

**Explanation:**
- Each permission has: name, description, resource, action
- Permissions are grouped by resource (PRODUIT, STOCK, etc.)
- Special permissions for validation and receiving

#### 4.2 Create Roles Seed Migration

**Location:** `src/main/resources/db/changelog/changes/v018-seed-roles.yaml`

```yaml
databaseChangeLog:
  - changeSet:
      id: v018-seed-roles
      author: tricol-security
      changes:
        - insert:
            tableName: role_app
            columns:
              - column: {name: name, value: "ADMIN"}
              - column: {name: description, value: "System administrator with full access"}
        
        - insert:
            tableName: role_app
            columns:
              - column: {name: name, value: "RESPONSABLE_ACHATS"}
              - column: {name: description, value: "Purchase manager"}
        
        - insert:
            tableName: role_app
            columns:
              - column: {name: name, value: "MAGASINIER"}
              - column: {name: description, value: "Warehouse keeper"}
        
        - insert:
            tableName: role_app
            columns:
              - column: {name: name, value: "CHEF_ATELIER"}
              - column: {name: description, value: "Workshop manager"}
```

#### 4.3 Create Role-Permission Mappings

**Location:** `src/main/resources/db/changelog/changes/v019-seed-role-permissions.yaml`

**Concept:** Assign default permissions to each role

```yaml
databaseChangeLog:
  - changeSet:
      id: v019-seed-role-permissions
      author: tricol-security
      changes:
        # ADMIN - All permissions (IDs 1-25)
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 1}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 2}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 3}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 4}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 5}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 6}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 7}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 8}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 9}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 10}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 11}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 12}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 13}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 14}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 15}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 16}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 17}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 18}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 19}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 20}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 21}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 22}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 23}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 24}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 1}, {name: permission_id, value: 25}]}
        
        # RESPONSABLE_ACHATS - Purchase management permissions
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 1}]}  # PRODUIT_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 2}]}  # PRODUIT_CREATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 3}]}  # PRODUIT_UPDATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 5}]}  # STOCK_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 7}]}  # FOURNISSEUR_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 8}]}  # FOURNISSEUR_CREATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 9}]}  # FOURNISSEUR_UPDATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 11}]} # COMMANDE_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 12}]} # COMMANDE_CREATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 13}]} # COMMANDE_UPDATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 14}]} # COMMANDE_VALIDATE
        
        # MAGASINIER - Warehouse operations
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 1}]}  # PRODUIT_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 5}]}  # STOCK_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 6}]}  # STOCK_MANAGE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 11}]} # COMMANDE_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 15}]} # COMMANDE_RECEIVE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 16}]} # BON_SORTIE_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 17}]} # BON_SORTIE_CREATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 18}]} # BON_SORTIE_UPDATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 19}]} # BON_SORTIE_VALIDATE
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 20}]} # BON_SORTIE_CANCEL
        
        # CHEF_ATELIER - Workshop operations
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 1}]}  # PRODUIT_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 5}]}  # STOCK_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 16}]} # BON_SORTIE_VIEW
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 21}]} # ATELIER_VIEW
```

**Explanation:**
- role_id 1 = ADMIN (all permissions)
- role_id 2 = RESPONSABLE_ACHATS (purchase-related)
- role_id 3 = MAGASINIER (warehouse operations)
- role_id 4 = CHEF_ATELIER (workshop view only)

#### 4.4 Create Admin User Seed

**Location:** `src/main/resources/db/changelog/changes/v020-seed-admin-user.yaml`

**Important:** Password must be BCrypt encrypted!

```yaml
databaseChangeLog:
  - changeSet:
      id: v020-seed-admin-user
      author: tricol-security
      changes:
        - insert:
            tableName: user_app
            columns:
              - column: {name: username, value: "admin"}
              - column: {name: email, value: "admin@tricol.com"}
              - column: {name: password, value: "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy"}
              - column: {name: enabled, valueBoolean: true}
              - column: {name: account_non_locked, valueBoolean: true}
              - column: {name: role_id, value: 1}
```

**Password:** The encrypted value is "password123"

**How to generate BCrypt hash:**
```java
// Use this in a test or main method
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hash = encoder.encode("password123");
System.out.println(hash);
```

#### 4.5 Update Master Changelog

**Location:** `src/main/resources/db/changelog/db.changelog-master.yaml`

Add the new seed changesets:

```yaml
databaseChangeLog:
  # ... existing changesets (v001-v016)
  
  - include:
      file: db/changelog/changes/v017-seed-permissions.yaml
  - include:
      file: db/changelog/changes/v018-seed-roles.yaml
  - include:
      file: db/changelog/changes/v019-seed-role-permissions.yaml
  - include:
      file: db/changelog/changes/v020-seed-admin-user.yaml
```

### ✅ Verification

1. **Run application:**
   ```bash
   mvn spring-boot:run
   ```

2. **Check permissions:**
   ```sql
   SELECT * FROM permission;
   ```
   Should see 25 permissions.

3. **Check roles:**
   ```sql
   SELECT * FROM role_app;
   ```
   Should see 4 roles.

4. **Check role-permission mappings:**
   ```sql
   SELECT r.name, COUNT(rp.permission_id) as permission_count
   FROM role_app r
   LEFT JOIN role_permission rp ON r.id = rp.role_id
   GROUP BY r.name;
   ```
   Should show:
   - ADMIN: 25 permissions
   - RESPONSABLE_ACHATS: 11 permissions
   - MAGASINIER: 10 permissions
   - CHEF_ATELIER: 4 permissions

5. **Check admin user:**
   ```sql
   SELECT u.username, u.email, r.name as role
   FROM user_app u
   JOIN role_app r ON u.role_id = r.id;
   ```
   Should see admin user with ADMIN role.

---

## 🎉 Phase 1 Complete!

You now have:
- ✅ JWT dependencies
- ✅ Security entities (User, Role, Permission, etc.)
- ✅ Database tables created
- ✅ Initial data seeded (roles, permissions, admin user)

**Next:** Phase 2 - JWT Infrastructure (Steps 5-7)

