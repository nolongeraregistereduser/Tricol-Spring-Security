# 🚀 PHASE 3: SEED INITIAL DATA

## MINI-PHASE 3.1: Seed Permissions (20 minutes)

**What:** Insert all 25 permissions into database

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v017-seed-permissions.yaml`
2. Copy this code:

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
        
        # USER_MANAGEMENT Permissions
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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 3.2: Seed Roles (10 minutes)

**What:** Insert 4 roles into database

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v018-seed-roles.yaml`
2. Copy this code:

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

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 3.3: Seed Role-Permission Mappings (15 minutes)

**What:** Assign default permissions to each role

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v019-seed-role-permissions.yaml`
2. Copy this code (this is long but necessary):

```yaml
databaseChangeLog:
  - changeSet:
      id: v019-seed-role-permissions
      author: tricol-security
      changes:
        # ADMIN - All 25 permissions
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
        
        # RESPONSABLE_ACHATS - Purchase management
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 1}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 2}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 3}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 5}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 7}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 8}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 9}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 11}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 12}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 13}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 2}, {name: permission_id, value: 14}]}
        
        # MAGASINIER - Warehouse operations
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 1}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 5}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 6}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 11}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 15}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 16}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 17}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 18}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 19}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 3}, {name: permission_id, value: 20}]}
        
        # CHEF_ATELIER - Workshop operations
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 1}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 5}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 16}]}
        - insert: {tableName: role_permission, columns: [{name: role_id, value: 4}, {name: permission_id, value: 21}]}
```

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 3.4: Seed Admin User (10 minutes)

**What:** Create first admin user

**Action:**
1. Create file: `src/main/resources/db/changelog/changes/v020-seed-admin-user.yaml`
2. Copy this code:

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

**Note:** Password is "password123" (BCrypt encrypted)

3. Save file
4. ✅ Verify: File created

---

## MINI-PHASE 3.5: Update Master Changelog (5 minutes)

**What:** Register seed migrations

**Action:**
1. Open file: `src/main/resources/db/changelog/db.changelog-master.yaml`
2. Add these lines at the end:

```yaml
  - include:
      file: db/changelog/changes/v017-seed-permissions.yaml
  - include:
      file: db/changelog/changes/v018-seed-roles.yaml
  - include:
      file: db/changelog/changes/v019-seed-role-permissions.yaml
  - include:
      file: db/changelog/changes/v020-seed-admin-user.yaml
```

3. Save file
4. ✅ Verify: File updated

---

## MINI-PHASE 3.6: Apply Seed Migrations (10 minutes)

**What:** Run application to seed data

**Action:**
1. Run: `mvn spring-boot:run`
2. Check console for Liquibase logs
3. Should see: "Successfully applied 4 changesets"
4. ✅ Verify in database:

```sql
-- Check permissions (should be 25)
SELECT COUNT(*) FROM permission;

-- Check roles (should be 4)
SELECT * FROM role_app;

-- Check role-permission mappings
SELECT r.name, COUNT(rp.permission_id) as permission_count
FROM role_app r
LEFT JOIN role_permission rp ON r.id = rp.role_id
GROUP BY r.name;

-- Should show:
-- ADMIN: 25
-- RESPONSABLE_ACHATS: 11
-- MAGASINIER: 10
-- CHEF_ATELIER: 4

-- Check admin user
SELECT u.username, u.email, r.name as role
FROM user_app u
JOIN role_app r ON u.role_id = r.id;
```

5. Stop application

---

## 🎉 PHASE 3 CHECKPOINT

**What you've completed:**
- ✅ Seeded 25 permissions
- ✅ Seeded 4 roles
- ✅ Mapped permissions to roles
- ✅ Created admin user
- ✅ Verified data in database

**Database is now ready!**

**Next:** Build JWT infrastructure

---
